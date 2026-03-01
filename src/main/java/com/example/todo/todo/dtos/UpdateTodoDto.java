package com.example.todo.todo.dtos;

import java.time.LocalDate;

import com.example.todo.todo.entities.Urgency;

public record UpdateTodoDto(
        String title,
        Boolean completed,
        Long categoryId,
        LocalDate dueDate,
        Urgency urgency,
        Integer recurrenceDays
) {
}