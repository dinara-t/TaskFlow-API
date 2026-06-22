package com.example.task.team.dtos;

import java.time.LocalDateTime;

import com.example.task.team.entities.TeamMember;
import com.example.task.team.entities.TeamMemberRole;

public record TeamMemberResponse(
        Long id,
        Long teamId,
        Long userId,
        String name,
        String email,
        TeamMemberRole role,
        LocalDateTime createdAt
) {
    public static TeamMemberResponse from(TeamMember member) {
        return new TeamMemberResponse(
                member.getId(),
                member.getTeam().getId(),
                member.getUser().getId(),
                member.getUser().getName(),
                member.getUser().getEmail(),
                member.getRole(),
                member.getCreatedAt()
        );
    }
}