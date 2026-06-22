package com.example.task.project;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.task.common.dto.PageResponse;
import com.example.task.project.dtos.CreateProjectDto;
import com.example.task.project.dtos.ProjectResponse;
import com.example.task.project.dtos.UpdateProjectDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectResponse> getProjects() {
        return projectService.getProjects();
    }

    @GetMapping("/paged")
    public PageResponse<ProjectResponse> getProjectsPaged(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return projectService.getProjectsPaged(page, size);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectDto dto) {
        ProjectResponse created = projectService.createProject(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ProjectResponse updateProject(@PathVariable Long id, @Valid @RequestBody UpdateProjectDto dto) {
        return projectService.updateProject(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}