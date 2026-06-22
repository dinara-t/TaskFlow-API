package com.example.task.team;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.task.team.dtos.AddTeamMemberDto;
import com.example.task.team.dtos.CreateTeamDto;
import com.example.task.team.dtos.TeamMemberResponse;
import com.example.task.team.dtos.TeamResponse;
import com.example.task.team.dtos.UpdateTeamDto;
import com.example.task.team.dtos.UpdateTeamMemberRoleDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public List<TeamResponse> getMyTeams() {
        return teamService.getMyTeams();
    }

    @GetMapping("/{id}")
    public TeamResponse getTeam(@PathVariable Long id) {
        return teamService.getTeam(id);
    }

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody CreateTeamDto dto) {
        TeamResponse created = teamService.createTeam(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public TeamResponse updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTeamDto dto
    ) {
        return teamService.updateTeam(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{teamId}/members")
    public List<TeamMemberResponse> getTeamMembers(@PathVariable Long teamId) {
        return teamService.getTeamMembers(teamId);
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<TeamMemberResponse> addTeamMember(
            @PathVariable Long teamId,
            @Valid @RequestBody AddTeamMemberDto dto
    ) {
        TeamMemberResponse created = teamService.addTeamMember(teamId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{teamId}/members/{memberId}/role")
    public TeamMemberResponse updateTeamMemberRole(
            @PathVariable Long teamId,
            @PathVariable Long memberId,
            @Valid @RequestBody UpdateTeamMemberRoleDto dto
    ) {
        return teamService.updateTeamMemberRole(teamId, memberId, dto);
    }

    @DeleteMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<Void> removeTeamMember(
            @PathVariable Long teamId,
            @PathVariable Long memberId
    ) {
        teamService.removeTeamMember(teamId, memberId);
        return ResponseEntity.noContent().build();
    }
}