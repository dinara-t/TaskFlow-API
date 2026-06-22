package com.example.task.project.dtos;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        Long teamId,
        String teamName
) {
}