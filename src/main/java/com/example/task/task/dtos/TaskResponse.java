package com.example.task.task.dtos;

import java.time.LocalDate;

import com.example.task.category.dtos.CategoryResponse;
import com.example.task.project.dtos.ProjectResponse;
import com.example.task.task.entities.Urgency;

public record TaskResponse(
        Long id,
        String title,
        boolean completed,
        boolean archived,
        LocalDate dueDate,
        Urgency urgency,
        Integer recurrenceDays,
        boolean overdue,
        CategoryResponse category,
        ProjectResponse project
) {
}