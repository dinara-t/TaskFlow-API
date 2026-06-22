package com.example.task.project;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.task.common.dto.PageResponse;
import com.example.task.common.exception.BadRequestException;
import com.example.task.common.exception.NotFoundException;
import com.example.task.common.serviceErrors.NotFoundError;
import com.example.task.common.serviceErrors.ValidationErrors;
import com.example.task.project.dtos.CreateProjectDto;
import com.example.task.project.dtos.ProjectResponse;
import com.example.task.project.dtos.UpdateProjectDto;
import com.example.task.project.entities.Project;

import com.example.task.activitylog.ActivityLogService;
import com.example.task.activitylog.entities.ActivityAction;
import com.example.task.activitylog.entities.ActivityEntityType;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ActivityLogService activityLogService;

    public ProjectService(
        ProjectRepository projectRepository,
    ActivityLogService activityLogService) {
        this.projectRepository = projectRepository;
        this.activityLogService= activityLogService;
    }

    public List<ProjectResponse> getProjects() {
        return projectRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public PageResponse<ProjectResponse> getProjectsPaged(Integer page, Integer size) {
        int safePage = page == null ? 0 : page;
        int safeSize = size == null ? 50 : size;

        ValidationErrors errors = new ValidationErrors();

        if (safePage < 0) {
            errors.addError("page", "Page must be 0 or greater");
        }

        if (safeSize <= 0) {
            errors.addError("size", "Size must be greater than 0");
        }

        if (safeSize > 200) {
            errors.addError("size", "Size must be 200 or less");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        PageRequest pageable = PageRequest.of(safePage, safeSize);
        Page<Project> projectPage = projectRepository.findAll(pageable);

        List<ProjectResponse> items = projectPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                items,
                projectPage.getNumber(),
                projectPage.getSize(),
                projectPage.getTotalElements(),
                projectPage.getTotalPages(),
                projectPage.hasNext(),
                projectPage.hasPrevious()
        );
    }

    public ProjectResponse createProject(CreateProjectDto dto) {
        ValidationErrors errors = new ValidationErrors();

        if (dto == null || dto.name() == null || dto.name().isBlank()) {
            errors.addError("name", "Name must not be blank");
        }

        if (dto != null && dto.name() != null
                && projectRepository.existsByNameIgnoreCase(dto.name().trim())) {
            errors.addError("name", "Project name already exists");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        Project project = new Project();
        project.setName(dto.name().trim());
        project.setDescription(cleanDescription(dto.description()));

        Project saved = projectRepository.save(project);
activityLogService.log(
        ActivityAction.PROJECT_CREATED,
        ActivityEntityType.PROJECT,
        saved.getId(),
        "Project created: " + saved.getName()
);
        return toResponse(saved);
    }

    public ProjectResponse updateProject(Long id, UpdateProjectDto dto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Project", id)));

        ValidationErrors errors = new ValidationErrors();

        if (dto == null || dto.name() == null || dto.name().isBlank()) {
            errors.addError("name", "Name must not be blank");
        }

        if (dto != null && dto.name() != null
                && projectRepository.existsByNameIgnoreCaseAndIdNot(dto.name().trim(), id)) {
            errors.addError("name", "Project name already exists");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        project.setName(dto.name().trim());
        project.setDescription(cleanDescription(dto.description()));

        Project saved = projectRepository.save(project);

activityLogService.log(
        ActivityAction.PROJECT_UPDATED,
        ActivityEntityType.PROJECT,
        saved.getId(),
        "Project updated: " + saved.getName()
);

        return toResponse(saved);
    }

    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Project", id)));

        projectRepository.delete(project);
        activityLogService.log(
        ActivityAction.PROJECT_DELETED,
        ActivityEntityType.PROJECT,
        id,
        "Project deleted: " + project.getName()
);
    }

    private String cleanDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription()
        );
    }
}