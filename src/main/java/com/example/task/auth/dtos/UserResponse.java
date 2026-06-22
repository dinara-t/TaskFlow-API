package com.example.task.auth.dtos;

import com.example.task.user.entities.Role;
import com.example.task.user.entities.User;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}