package com.example.task.project;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.task.activitylog.ActivityLogService;
import com.example.task.activitylog.entities.ActivityAction;
import com.example.task.activitylog.entities.ActivityEntityType;
import com.example.task.common.dto.PageResponse;
import com.example.task.common.exception.BadRequestException;
import com.example.task.common.exception.NotFoundException;
import com.example.task.common.serviceErrors.NotFoundError;
import com.example.task.common.serviceErrors.ValidationErrors;
import com.example.task.project.dtos.CreateProjectDto;
import com.example.task.project.dtos.ProjectResponse;
import com.example.task.project.dtos.UpdateProjectDto;
import com.example.task.project.entities.Project;
import com.example.task.team.TeamMemberRepository;
import com.example.task.team.TeamRepository;
import com.example.task.team.entities.Team;
import com.example.task.team.entities.TeamMember;
import com.example.task.team.entities.TeamMemberRole;
import com.example.task.user.UserRepository;
import com.example.task.user.entities.User;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public ProjectService(
            ProjectRepository projectRepository,
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            UserRepository userRepository,
            ActivityLogService activityLogService
    ) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

public List<ProjectResponse> getProjects(Long teamId) {
    User currentUser = getCurrentUser();

    if (teamId == null) {
        return teamMemberRepository.findByUser_EmailIgnoreCaseOrderByTeam_NameAsc(currentUser.getEmail())
                .stream()
                .flatMap(member -> projectRepository.findByTeamIdOrderByNameAsc(member.getTeam().getId()).stream())
                .map(this::toResponse)
                .toList();
    }

    getMembershipOrThrow(teamId, currentUser.getEmail());

    return projectRepository.findByTeamIdOrderByNameAsc(teamId)
            .stream()
            .map(this::toResponse)
            .toList();
}

    public PageResponse<ProjectResponse> getProjectsPaged(Long teamId, Integer page, Integer size) {
        User currentUser = getCurrentUser();

        if (teamId == null) {
            ValidationErrors errors = new ValidationErrors();
            errors.addError("teamId", "Team ID is required");
            throw BadRequestException.from(errors);
        }

        getMembershipOrThrow(teamId, currentUser.getEmail());

        int safePage = page == null ? 0 : page;
        int safeSize = size == null ? 50 : size;

        ValidationErrors errors = new ValidationErrors();

        if (safePage < 0) {
            errors.addError("page", "Page must be 0 or greater");
        }

        if (safeSize <= 0) {
            errors.addError("size", "Size must be greater than 0");
        }

        if (safeSize > 200) {
            errors.addError("size", "Size must be 200 or less");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        PageRequest pageable = PageRequest.of(safePage, safeSize);
        Page<Project> projectPage = projectRepository.findAll((root, query, cb) ->
                cb.equal(root.get("team").get("id"), teamId), pageable);

        List<ProjectResponse> items = projectPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                items,
                projectPage.getNumber(),
                projectPage.getSize(),
                projectPage.getTotalElements(),
                projectPage.getTotalPages(),
                projectPage.hasNext(),
                projectPage.hasPrevious()
        );
    }

    public ProjectResponse getProject(Long id) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Project", id)));

        getMembershipOrThrow(project.getTeam().getId(), currentUser.getEmail());

        return toResponse(project);
    }

    public ProjectResponse createProject(CreateProjectDto dto) {
        User currentUser = getCurrentUser();
        ValidationErrors errors = new ValidationErrors();

        if (dto == null) {
            errors.addError("project", "Project payload is required");
            throw BadRequestException.from(errors);
        }

        if (dto.name() == null || dto.name().isBlank()) {
            errors.addError("name", "Name must not be blank");
        }

        if (dto.teamId() == null) {
            errors.addError("teamId", "Team ID is required");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        TeamMember membership = getMembershipOrThrow(dto.teamId(), currentUser.getEmail());
        requireOwnerOrAdmin(membership);

        Team team = teamRepository.findById(dto.teamId())
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Team", dto.teamId())));

        String name = dto.name().trim();

        if (projectRepository.existsByTeamIdAndNameIgnoreCase(team.getId(), name)) {
            ValidationErrors duplicateErrors = new ValidationErrors();
            duplicateErrors.addError("name", "Project name already exists in this team");
            throw BadRequestException.from(duplicateErrors);
        }

        Project project = new Project();
        project.setName(name);
        project.setDescription(cleanDescription(dto.description()));
        project.setTeam(team);

        Project saved = projectRepository.save(project);

        activityLogService.log(
                ActivityAction.PROJECT_CREATED,
                ActivityEntityType.PROJECT,
                saved.getId(),
                "Project created: " + saved.getName()
        );

        return toResponse(saved);
    }

    public ProjectResponse updateProject(Long id, UpdateProjectDto dto) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Project", id)));

        TeamMember membership = getMembershipOrThrow(project.getTeam().getId(), currentUser.getEmail());
        requireOwnerOrAdmin(membership);

        ValidationErrors errors = new ValidationErrors();

        if (dto == null || dto.name() == null || dto.name().isBlank()) {
            errors.addError("name", "Name must not be blank");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        String name = dto.name().trim();

        if (projectRepository.existsByTeamIdAndNameIgnoreCaseAndIdNot(
                project.getTeam().getId(),
                name,
                id
        )) {
            ValidationErrors duplicateErrors = new ValidationErrors();
            duplicateErrors.addError("name", "Project name already exists in this team");
            throw BadRequestException.from(duplicateErrors);
        }

        project.setName(name);
        project.setDescription(cleanDescription(dto.description()));

        Project saved = projectRepository.save(project);

        activityLogService.log(
                ActivityAction.PROJECT_UPDATED,
                ActivityEntityType.PROJECT,
                saved.getId(),
                "Project updated: " + saved.getName()
        );

        return toResponse(saved);
    }

    public void deleteProject(Long id) {
        User currentUser = getCurrentUser();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Project", id)));

        TeamMember membership = getMembershipOrThrow(project.getTeam().getId(), currentUser.getEmail());
        requireOwnerOrAdmin(membership);

        String projectName = project.getName();

        projectRepository.delete(project);

        activityLogService.log(
                ActivityAction.PROJECT_DELETED,
                ActivityEntityType.PROJECT,
                id,
                "Project deleted: " + projectName
        );
    }

    private ProjectResponse toResponse(Project project) {
        Team team = project.getTeam();

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                team == null ? null : team.getId(),
                team == null ? null : team.getName()
        );
    }

    private TeamMember getMembershipOrThrow(Long teamId, String email) {
        return teamMemberRepository.findByTeamIdAndUser_EmailIgnoreCase(teamId, email)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Team", teamId)));
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