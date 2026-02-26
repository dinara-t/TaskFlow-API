package com.example.todo.todo.dtos;

import com.example.todo.category.dtos.CategoryResponse;

public record TodoResponse(
    Long id,
    String title,
    boolean completed,
    CategoryResponse category
) {}