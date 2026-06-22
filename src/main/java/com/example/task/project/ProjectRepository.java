package com.example.task.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.task.project.entities.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query("select coalesce(max(p.id), 0) from Project p")
    Long getMaxId();
}