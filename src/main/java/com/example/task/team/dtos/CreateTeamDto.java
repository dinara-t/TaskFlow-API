package com.example.task.team.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTeamDto(
        @NotBlank(message = "Team name must not be blank")
        @Size(min = 2, max = 100, message = "Team name must be between 2 and 100 characters")
        String name,

        @Size(max = 500, message = "Description must be 500 characters or less")
        String description
) {
}