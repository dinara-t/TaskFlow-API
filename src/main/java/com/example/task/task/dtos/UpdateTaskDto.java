package com.example.task.task.dtos;

import java.time.LocalDate;

import com.example.task.task.entities.Urgency;

public record UpdateTaskDto(
        String title,
        Boolean completed,
        Long categoryId,
        Long projectId,
        LocalDate dueDate,
        Urgency urgency,
        Integer recurrenceDays
) {
}