package com.example.task.task;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.task.category.CategoryRepository;
import com.example.task.category.dtos.CategoryResponse;
import com.example.task.category.entities.Category;
import com.example.task.common.dto.PageResponse;
import com.example.task.common.dto.SortOrder;
import com.example.task.common.exception.BadRequestException;
import com.example.task.common.exception.NotFoundException;
import com.example.task.common.serviceErrors.NotFoundError;
import com.example.task.common.serviceErrors.ValidationErrors;
import com.example.task.project.ProjectRepository;
import com.example.task.project.dtos.ProjectResponse;
import com.example.task.project.entities.Project;
import com.example.task.task.dtos.CreateTaskDto;
import com.example.task.task.dtos.TaskQueryParams;
import com.example.task.task.dtos.TaskResponse;
import com.example.task.task.dtos.UpdateTaskDto;
import com.example.task.task.entities.Task;

@Service
public class TaskService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt",
            "title",
            "completed",
            "dueDate",
            "urgency"
    );

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final ProjectRepository projectRepository;

    public TaskService(
            TaskRepository taskRepository,
            CategoryRepository categoryRepository,
            ProjectRepository projectRepository
    ) {
        this.taskRepository = taskRepository;
        this.categoryRepository = categoryRepository;
        this.projectRepository = projectRepository;
    }

    public List<TaskResponse> getTasks(TaskQueryParams params, Long categoryId, Long projectId) {
        Sort sort = buildSort(params);
        Specification<Task> spec = buildSpec(params, categoryId, projectId);

        return taskRepository.findAll(spec, sort).stream()
                .map(this::toResponse)
                .toList();
    }

    public PageResponse<TaskResponse> getTasksPaged(
            TaskQueryParams params,
            Long categoryId,
            Long projectId,
            Integer page,
            Integer size
    ) {
        int safePage = page == null ? 0 : page;
        int safeSize = size == null ? 20 : size;

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

        Sort sort = buildSort(params);
        Specification<Task> spec = buildSpec(params, categoryId, projectId);
        PageRequest pageable = PageRequest.of(safePage, safeSize, sort);

        Page<Task> taskPage = taskRepository.findAll(spec, pageable);

        List<TaskResponse> items = taskPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                items,
                taskPage.getNumber(),
                taskPage.getSize(),
                taskPage.getTotalElements(),
                taskPage.getTotalPages(),
                taskPage.hasNext(),
                taskPage.hasPrevious()
        );
    }

    public TaskResponse createTask(CreateTaskDto dto) {
        ValidationErrors errors = new ValidationErrors();

        if (dto == null) {
            errors.addError("task", "Task payload is required");
            throw BadRequestException.from(errors);
        }

        if (dto.title() == null || dto.title().isBlank()) {
            errors.addError("title", "Title must not be blank");
        }

        if (dto.categoryId() == null) {
            errors.addError("categoryId", "Category ID is required");
        }

        if (dto.recurrenceDays() != null && dto.recurrenceDays() < 0) {
            errors.addError("recurrenceDays", "Recurrence days must be 0 or positive");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Category", dto.categoryId())));

        Project project = null;
        if (dto.projectId() != null) {
            project = projectRepository.findById(dto.projectId())
                    .orElseThrow(() -> new NotFoundException(new NotFoundError("Project", dto.projectId())));
        }

        Task task = new Task();
        task.setTitle(dto.title().trim());
        task.setCompleted(Boolean.TRUE.equals(dto.completed()));
        task.setArchived(false);
        task.setCategory(category);
        task.setProject(project);
        task.setDueDate(dto.dueDate());

        if (dto.urgency() != null) {
            task.setUrgency(dto.urgency());
        }

        task.setRecurrenceDays(normaliseRecurrenceDays(dto.recurrenceDays()));

        Task saved = taskRepository.save(task);

        return toResponse(saved);
    }

    public TaskResponse updateTask(Long id, UpdateTaskDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Task", id)));

        if (dto == null) {
            return toResponse(task);
        }

        ValidationErrors errors = new ValidationErrors();

        if (dto.title() != null && dto.title().isBlank()) {
            errors.addError("title", "Title must not be blank");
        }

        if (dto.categoryId() != null && dto.categoryId() <= 0) {
            errors.addError("categoryId", "Category ID must be positive");
        }

        if (dto.projectId() != null && dto.projectId() <= 0) {
            errors.addError("projectId", "Project ID must be positive");
        }

        if (dto.recurrenceDays() != null && dto.recurrenceDays() < 0) {
            errors.addError("recurrenceDays", "Recurrence days must be 0 or positive");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        if (dto.title() != null) {
            task.setTitle(dto.title().trim());
        }

        if (dto.completed() != null) {
            task.setCompleted(dto.completed());
        }

        if (dto.categoryId() != null) {
            Category category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new NotFoundException(new NotFoundError("Category", dto.categoryId())));

            task.setCategory(category);
        }

        if (dto.projectId() != null) {
            Project project = projectRepository.findById(dto.projectId())
                    .orElseThrow(() -> new NotFoundException(new NotFoundError("Project", dto.projectId())));

            task.setProject(project);
        }

        if (dto.dueDate() != null) {
            task.setDueDate(dto.dueDate());
        }

        if (dto.urgency() != null) {
            task.setUrgency(dto.urgency());
        }

        if (dto.recurrenceDays() != null) {
            task.setRecurrenceDays(normaliseRecurrenceDays(dto.recurrenceDays()));
        }

        Task saved = taskRepository.save(task);

        return toResponse(saved);
    }

    public void archiveTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Task", id)));

        task.setArchived(true);
        taskRepository.save(task);
    }

    public TaskResponse duplicateTask(Long id, Integer shiftDays) {
        Task original = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Task", id)));

        int safeShiftDays = shiftDays == null ? 0 : shiftDays;

        Task duplicate = new Task();
        duplicate.setTitle(original.getTitle());
        duplicate.setCompleted(false);
        duplicate.setArchived(false);
        duplicate.setCategory(original.getCategory());
        duplicate.setProject(original.getProject());
        duplicate.setUrgency(original.getUrgency());
        duplicate.setRecurrenceDays(original.getRecurrenceDays());

        if (original.getDueDate() != null) {
            duplicate.setDueDate(original.getDueDate().plusDays(safeShiftDays));
        }

        Task saved = taskRepository.save(duplicate);

        return toResponse(saved);
    }

    private Specification<Task> buildSpec(TaskQueryParams params, Long categoryId, Long projectId) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.isFalse(root.get("isArchived")));

            if (categoryId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("category").get("id"), categoryId));
            }

            if (projectId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("project").get("id"), projectId));
            }

            if (params == null) {
                return predicate;
            }

            if (params.completed() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("completed"), params.completed()));
            }

            if (params.urgency() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("urgency"), params.urgency()));
            }

            if (params.dueAfter() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("dueDate"), params.dueAfter()));
            }

            if (params.dueBefore() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("dueDate"), params.dueBefore()));
            }

            if (Boolean.TRUE.equals(params.overdue())) {
                predicate = cb.and(
                        predicate,
                        cb.isFalse(root.get("completed")),
                        cb.lessThan(root.get("dueDate"), LocalDate.now())
                );
            }

            return predicate;
        };
    }

    private Sort buildSort(TaskQueryParams params) {
        String sortBy = params == null || params.sortBy() == null || params.sortBy().isBlank()
                ? "createdAt"
                : params.sortBy();

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            ValidationErrors errors = new ValidationErrors();
            errors.addError("sortBy", "Sort field is not supported");
            throw BadRequestException.from(errors);
        }

        SortOrder order = params == null ? SortOrder.DESC : params.orderOrDefault();

        Sort.Direction direction = order == SortOrder.ASC
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, sortBy);
    }

    private Integer normaliseRecurrenceDays(Integer recurrenceDays) {
        if (recurrenceDays == null || recurrenceDays == 0) {
            return null;
        }

        return recurrenceDays;
    }

    private boolean isOverdue(Task task) {
        return task.getDueDate() != null
                && !task.isCompleted()
                && task.getDueDate().isBefore(LocalDate.now());
    }

    private TaskResponse toResponse(Task task) {
        CategoryResponse category = new CategoryResponse(
                task.getCategory().getId(),
                task.getCategory().getName()
        );

        ProjectResponse project = null;
        if (task.getProject() != null) {
            project = new ProjectResponse(
                    task.getProject().getId(),
                    task.getProject().getName(),
                    task.getProject().getDescription()
            );
        }

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.isCompleted(),
                task.isArchived(),
                task.getDueDate(),
                task.getUrgency(),
                task.getRecurrenceDays(),
                isOverdue(task),
                category,
                project
        );
    }
}