package com.example.task.team;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.task.activitylog.ActivityLogService;
import com.example.task.activitylog.entities.ActivityAction;
import com.example.task.activitylog.entities.ActivityEntityType;
import com.example.task.common.exception.BadRequestException;
import com.example.task.common.exception.NotFoundException;
import com.example.task.common.serviceErrors.NotFoundError;
import com.example.task.common.serviceErrors.ValidationErrors;
import com.example.task.team.dtos.AddTeamMemberDto;
import com.example.task.team.dtos.CreateTeamDto;
import com.example.task.team.dtos.TeamMemberResponse;
import com.example.task.team.dtos.TeamResponse;
import com.example.task.team.dtos.UpdateTeamDto;
import com.example.task.team.dtos.UpdateTeamMemberRoleDto;
import com.example.task.team.entities.Team;
import com.example.task.team.entities.TeamMember;
import com.example.task.team.entities.TeamMemberRole;
import com.example.task.user.UserRepository;
import com.example.task.user.entities.User;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public TeamService(
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            UserRepository userRepository,
            ActivityLogService activityLogService
    ) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    public List<TeamResponse> getMyTeams() {
        User currentUser = getCurrentUser();

        return teamMemberRepository.findByUser_EmailIgnoreCaseOrderByTeam_NameAsc(currentUser.getEmail())
                .stream()
                .map(member -> toResponse(member.getTeam(), member.getRole()))
                .toList();
    }

    public TeamResponse getTeam(Long id) {
        User currentUser = getCurrentUser();
        TeamMember membership = getMembershipOrThrow(id, currentUser.getEmail());

        return toResponse(membership.getTeam(), membership.getRole());
    }

    public TeamResponse createTeam(CreateTeamDto dto) {
        User currentUser = getCurrentUser();
        ValidationErrors errors = new ValidationErrors();

        if (dto == null || dto.name() == null || dto.name().isBlank()) {
            errors.addError("name", "Team name must not be blank");
        }

        String name = dto == null || dto.name() == null ? null : dto.name().trim();

        if (name != null && teamRepository.existsByNameIgnoreCaseAndOwnerId(name, currentUser.getId())) {
            errors.addError("name", "You already own a team with this name");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        Team team = new Team();
        team.setName(name);
        team.setDescription(cleanDescription(dto.description()));
        team.setOwner(currentUser);

        Team savedTeam = teamRepository.save(team);

        TeamMember ownerMember = new TeamMember();
        ownerMember.setTeam(savedTeam);
        ownerMember.setUser(currentUser);
        ownerMember.setRole(TeamMemberRole.OWNER);

        teamMemberRepository.save(ownerMember);

        activityLogService.log(
                ActivityAction.TEAM_CREATED,
                ActivityEntityType.TEAM,
                savedTeam.getId(),
                "Team created: " + savedTeam.getName()
        );

        return toResponse(savedTeam, TeamMemberRole.OWNER);
    }

    public TeamResponse updateTeam(Long id, UpdateTeamDto dto) {
        User currentUser = getCurrentUser();
        TeamMember membership = getMembershipOrThrow(id, currentUser.getEmail());

        requireOwner(membership);

        Team team = membership.getTeam();

        ValidationErrors errors = new ValidationErrors();

        if (dto == null || dto.name() == null || dto.name().isBlank()) {
            errors.addError("name", "Team name must not be blank");
        }

        String name = dto == null || dto.name() == null ? null : dto.name().trim();

        if (name != null && teamRepository.existsByNameIgnoreCaseAndOwnerIdAndIdNot(
                name,
                currentUser.getId(),
                team.getId()
        )) {
            errors.addError("name", "You already own a team with this name");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        team.setName(name);
        team.setDescription(cleanDescription(dto.description()));

        Team saved = teamRepository.save(team);

        activityLogService.log(
                ActivityAction.TEAM_UPDATED,
                ActivityEntityType.TEAM,
                saved.getId(),
                "Team updated: " + saved.getName()
        );

        return toResponse(saved, membership.getRole());
    }

    public void deleteTeam(Long id) {
        User currentUser = getCurrentUser();
        TeamMember membership = getMembershipOrThrow(id, currentUser.getEmail());

        requireOwner(membership);

        String teamName = membership.getTeam().getName();

        teamRepository.delete(membership.getTeam());

        activityLogService.log(
                ActivityAction.TEAM_DELETED,
                ActivityEntityType.TEAM,
                id,
                "Team deleted: " + teamName
        );
    }

    public List<TeamMemberResponse> getTeamMembers(Long teamId) {
        User currentUser = getCurrentUser();
        getMembershipOrThrow(teamId, currentUser.getEmail());

        return teamMemberRepository.findByTeamIdOrderByCreatedAtAsc(teamId)
                .stream()
                .map(TeamMemberResponse::from)
                .toList();
    }

    public TeamMemberResponse addTeamMember(Long teamId, AddTeamMemberDto dto) {
        User currentUser = getCurrentUser();
        TeamMember currentMembership = getMembershipOrThrow(teamId, currentUser.getEmail());

        requireOwnerOrAdmin(currentMembership);

        ValidationErrors errors = new ValidationErrors();

        if (dto == null || dto.email() == null || dto.email().isBlank()) {
            errors.addError("email", "Email must not be blank");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        String email = dto.email().trim().toLowerCase();

        User userToAdd = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> {
                    ValidationErrors userErrors = new ValidationErrors();
                    userErrors.addError("email", "User with this email does not exist");
                    return BadRequestException.from(userErrors);
                });

        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, userToAdd.getId())) {
            ValidationErrors duplicateErrors = new ValidationErrors();
            duplicateErrors.addError("email", "User is already a member of this team");
            throw BadRequestException.from(duplicateErrors);
        }

        Team team = currentMembership.getTeam();

        TeamMemberRole role = dto.role() == null ? TeamMemberRole.MEMBER : dto.role();

        if (role == TeamMemberRole.OWNER) {
            ValidationErrors roleErrors = new ValidationErrors();
            roleErrors.addError("role", "Use MEMBER or ADMIN when adding a team member");
            throw BadRequestException.from(roleErrors);
        }

        TeamMember member = new TeamMember();
        member.setTeam(team);
        member.setUser(userToAdd);
        member.setRole(role);

        TeamMember saved = teamMemberRepository.save(member);

        activityLogService.log(
                ActivityAction.TEAM_MEMBER_ADDED,
                ActivityEntityType.TEAM,
                team.getId(),
                "Team member added: " + userToAdd.getEmail()
        );

        return TeamMemberResponse.from(saved);
    }

    public TeamMemberResponse updateTeamMemberRole(
            Long teamId,
            Long memberId,
            UpdateTeamMemberRoleDto dto
    ) {
        User currentUser = getCurrentUser();
        TeamMember currentMembership = getMembershipOrThrow(teamId, currentUser.getEmail());

        requireOwner(currentMembership);

        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Team member", memberId)));

        if (!member.getTeam().getId().equals(teamId)) {
            throw new NotFoundException(new NotFoundError("Team member", memberId));
        }

        if (member.getRole() == TeamMemberRole.OWNER) {
            ValidationErrors errors = new ValidationErrors();
            errors.addError("role", "Owner role cannot be changed");
            throw BadRequestException.from(errors);
        }

        if (dto == null || dto.role() == null) {
            ValidationErrors errors = new ValidationErrors();
            errors.addError("role", "Role is required");
            throw BadRequestException.from(errors);
        }

        if (dto.role() == TeamMemberRole.OWNER) {
            ValidationErrors errors = new ValidationErrors();
            errors.addError("role", "Owner role cannot be assigned here");
            throw BadRequestException.from(errors);
        }

        member.setRole(dto.role());

        TeamMember saved = teamMemberRepository.save(member);

        activityLogService.log(
                ActivityAction.TEAM_MEMBER_ROLE_UPDATED,
                ActivityEntityType.TEAM,
                teamId,
                "Team member role updated: " + saved.getUser().getEmail()
        );

        return TeamMemberResponse.from(saved);
    }

    public void removeTeamMember(Long teamId, Long memberId) {
        User currentUser = getCurrentUser();
        TeamMember currentMembership = getMembershipOrThrow(teamId, currentUser.getEmail());

        requireOwner(currentMembership);

        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Team member", memberId)));

        if (!member.getTeam().getId().equals(teamId)) {
            throw new NotFoundException(new NotFoundError("Team member", memberId));
        }

        if (member.getRole() == TeamMemberRole.OWNER) {
            ValidationErrors errors = new ValidationErrors();
            errors.addError("member", "Team owner cannot be removed");
            throw BadRequestException.from(errors);
        }

        String email = member.getUser().getEmail();

        teamMemberRepository.delete(member);

        activityLogService.log(
                ActivityAction.TEAM_MEMBER_REMOVED,
                ActivityEntityType.TEAM,
                teamId,
                "Team member removed: " + email
        );
    }

    private TeamResponse toResponse(Team team, TeamMemberRole currentUserRole) {
        long memberCount = teamMemberRepository.countByTeamId(team.getId());
        return TeamResponse.from(team, currentUserRole, memberCount);
    }

    private TeamMember getMembershipOrThrow(Long teamId, String email) {
        return teamMemberRepository.findByTeamIdAndUser_EmailIgnoreCase(teamId, email)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Team", teamId)));
    }

    private void requireOwner(TeamMember membership) {
        if (membership.getRole() != TeamMemberRole.OWNER) {
            ValidationErrors errors = new ValidationErrors();
            errors.addError("permission", "Only the team owner can perform this action");
            throw BadRequestException.from(errors);
        }
    }

    private void requireOwnerOrAdmin(TeamMember membership) {
        if (membership.getRole() != TeamMemberRole.OWNER && membership.getRole() != TeamMemberRole.ADMIN) {
            ValidationErrors errors = new ValidationErrors();
            errors.addError("permission", "Only team owners or admins can perform this action");
            throw BadRequestException.from(errors);
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null || "anonymousUser".equals(authentication.getName())) {
            ValidationErrors errors = new ValidationErrors();
            errors.addError("auth", "User is not authenticated");
            throw BadRequestException.from(errors);
        }

        return userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> {
                    ValidationErrors errors = new ValidationErrors();
                    errors.addError("auth", "Authenticated user was not found");
                    return BadRequestException.from(errors);
                });
    }

    private String cleanDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }
}