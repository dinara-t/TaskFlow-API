package com.example.task.team;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.task.team.entities.TeamMember;
import com.example.task.team.entities.TeamMemberRole;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByUser_EmailIgnoreCaseOrderByTeam_NameAsc(String email);

    List<TeamMember> findByTeamIdOrderByCreatedAtAsc(Long teamId);

    Optional<TeamMember> findByTeamIdAndUser_EmailIgnoreCase(Long teamId, String email);

    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);

    long countByTeamId(Long teamId);

    long countByTeamIdAndRole(Long teamId, TeamMemberRole role);
}