package com.example.task.activitylog.dtos;

import java.time.LocalDateTime;

import com.example.task.activitylog.entities.ActivityAction;
import com.example.task.activitylog.entities.ActivityEntityType;
import com.example.task.activitylog.entities.ActivityLog;

public record ActivityLogResponse(
        Long id,
        ActivityAction action,
        ActivityEntityType entityType,
        Long entityId,
        String message,
        Long userId,
        String userEmail,
        LocalDateTime createdAt
) {
    public static ActivityLogResponse from(ActivityLog log) {
        return new ActivityLogResponse(
                log.getId(),
                log.getAction(),
                log.getEntityType(),
                log.getEntityId(),
                log.getMessage(),
                log.getUser() == null ? null : log.getUser().getId(),
                log.getUser() == null ? null : log.getUser().getEmail(),
                log.getCreatedAt()
        );
    }
}