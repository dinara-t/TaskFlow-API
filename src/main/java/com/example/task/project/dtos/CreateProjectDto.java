package com.example.task.project.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectDto(
        @NotBlank(message = "Name must not be blank")
        @Size(min = 2, message = "Name must be longer than 1 character")
        String name,

        @Size(max = 500, message = "Description must be 500 characters or less")
        String description
) {
}