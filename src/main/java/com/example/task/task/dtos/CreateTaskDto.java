package com.example.task.task.dtos;

import java.time.LocalDate;

import com.example.task.task.entities.Urgency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateTaskDto(
        @NotBlank(message = "Title must not be blank")
        @Size(min = 2, message = "Title must be longer than 1 character")
        String title,

        @NotNull(message = "Completed must be true or false")
        Boolean completed,

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be positive")
        Long categoryId,

        @Positive(message = "Project ID must be positive")
        Long projectId,

        LocalDate dueDate,

        Urgency urgency,

        @PositiveOrZero(message = "Recurrence days must be 0 or positive")
        Integer recurrenceDays
) {
}