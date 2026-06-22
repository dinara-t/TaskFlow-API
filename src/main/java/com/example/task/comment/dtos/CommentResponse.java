package com.example.task.comment.dtos;

import java.time.LocalDateTime;

import com.example.task.comment.entities.Comment;

public record CommentResponse(
        Long id,
        Long taskId,
        String body,
        Long userId,
        String userEmail,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getTask().getId(),
                comment.getBody(),
                comment.getUser() == null ? null : comment.getUser().getId(),
                comment.getUser() == null ? null : comment.getUser().getEmail(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}