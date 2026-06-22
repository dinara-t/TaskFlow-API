package com.example.task.auth.dtos;

public record AuthResponse(
        String token,
        UserResponse user
) {
}