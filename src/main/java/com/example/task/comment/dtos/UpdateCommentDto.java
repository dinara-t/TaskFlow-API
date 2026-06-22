package com.example.task.comment.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCommentDto(
        @NotBlank(message = "Comment body must not be blank")
        @Size(max = 1000, message = "Comment body must be 1000 characters or less")
        String body
) {
}