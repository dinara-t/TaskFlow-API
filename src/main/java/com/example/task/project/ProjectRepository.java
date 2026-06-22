package com.example.task.project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.task.project.entities.Project;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    List<Project> findByTeamIdOrderByNameAsc(Long teamId);

    boolean existsByTeamIdAndNameIgnoreCase(Long teamId, String name);

    boolean existsByTeamIdAndNameIgnoreCaseAndIdNot(Long teamId, String name, Long id);

    @Query("select coalesce(max(p.id), 0) from Project p")
    Long getMaxId();
}