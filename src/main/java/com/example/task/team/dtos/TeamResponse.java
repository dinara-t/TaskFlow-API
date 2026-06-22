package com.example.task.team.dtos;

import java.time.LocalDateTime;

import com.example.task.team.entities.Team;
import com.example.task.team.entities.TeamMemberRole;

public record TeamResponse(
        Long id,
        String name,
        String description,
        Long ownerId,
        String ownerEmail,
        TeamMemberRole currentUserRole,
        long memberCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TeamResponse from(
            Team team,
            TeamMemberRole currentUserRole,
            long memberCount
    ) {
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getDescription(),
                team.getOwner().getId(),
                team.getOwner().getEmail(),
                currentUserRole,
                memberCount,
                team.getCreatedAt(),
                team.getUpdatedAt()
        );
    }
}