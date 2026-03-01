package com.example.todo.todo.dtos;

import java.time.LocalDate;

import com.example.todo.category.dtos.CategoryResponse;
import com.example.todo.todo.entities.Urgency;

public record TodoResponse(
        Long id,
        String title,
        boolean completed,
        boolean archived,
        LocalDate dueDate,
        Urgency urgency,
        Integer recurrenceDays,
        boolean overdue,
        CategoryResponse category
) {}