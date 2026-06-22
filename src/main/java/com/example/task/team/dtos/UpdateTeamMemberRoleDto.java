package com.example.task.team.dtos;

import com.example.task.team.entities.TeamMemberRole;

import jakarta.validation.constraints.NotNull;

public record UpdateTeamMemberRoleDto(
        @NotNull(message = "Role is required")
        TeamMemberRole role
) {
}