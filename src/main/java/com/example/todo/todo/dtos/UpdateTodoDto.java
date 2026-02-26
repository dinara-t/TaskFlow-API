package com.example.todo.todo.dtos;

public record UpdateTodoDto(
        String title,
        Boolean completed,
        Long categoryId
) {
}