package com.example.task.team;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.task.team.entities.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByNameIgnoreCaseAndOwnerId(String name, Long ownerId);

    boolean existsByNameIgnoreCaseAndOwnerIdAndIdNot(String name, Long ownerId, Long id);
}