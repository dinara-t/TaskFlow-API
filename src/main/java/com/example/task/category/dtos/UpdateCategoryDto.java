package com.example.task.category.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryDto(
        @NotBlank(message = "Name must not be blank")
        @Size(min = 2, message = "Name must be longer than 1 character")
        String name
) {
}