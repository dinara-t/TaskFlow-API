package com.example.task.team.dtos;

import com.example.task.team.entities.TeamMemberRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddTeamMemberDto(
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be valid")
        String email,

        TeamMemberRole role
) {
}