package com.example.todo.todo;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.todo.category.CategoryRepository;
import com.example.todo.category.dtos.CategoryResponse;
import com.example.todo.category.entities.Category;
import com.example.todo.common.dto.PageResponse;
import com.example.todo.common.dto.SortOrder;
import com.example.todo.common.exception.BadRequestException;
import com.example.todo.common.exception.NotFoundException;
import com.example.todo.common.serviceErrors.NotFoundError;
import com.example.todo.common.serviceErrors.ValidationErrors;
import com.example.todo.todo.dtos.CreateTodoDto;
import com.example.todo.todo.dtos.TodoQueryParams;
import com.example.todo.todo.dtos.TodoResponse;
import com.example.todo.todo.dtos.UpdateTodoDto;
import com.example.todo.todo.entities.Todo;
import com.example.todo.todo.entities.Urgency;

@Service
public class TodoService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "createdAt",
            "title",
            "completed",
            "dueDate",
            "urgency"
    );

    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;

    public TodoService(TodoRepository todoRepository, CategoryRepository categoryRepository) {
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<TodoResponse> getTodos(TodoQueryParams params, Long categoryId) {
        Sort sort = buildSort(params);
        Specification<Todo> spec = buildSpec(params, categoryId);
        return todoRepository.findAll(spec, sort).stream().map(this::toResponse).toList();
    }

    public PageResponse<TodoResponse> getTodosPaged(TodoQueryParams params, Long categoryId, Integer page, Integer size) {
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
        PageRequest pageable = PageRequest.of(safePage, safeSize, sort);

        Specification<Todo> spec = buildSpec(params, categoryId);
        Page<Todo> todoPage = todoRepository.findAll(spec, pageable);

        List<TodoResponse> items = todoPage.getContent().stream().map(this::toResponse).toList();

        return new PageResponse<>(
                items,
                todoPage.getNumber(),
                todoPage.getSize(),
                todoPage.getTotalElements(),
                todoPage.getTotalPages(),
                todoPage.hasNext(),
                todoPage.hasPrevious()
        );
    }

    public TodoResponse createTodo(CreateTodoDto dto) {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Category", dto.categoryId())));

        Todo todo = new Todo();
        todo.setTitle(dto.title().trim());
        todo.setCompleted(Boolean.TRUE.equals(dto.completed()));
        todo.setArchived(false);
        todo.setCategory(category);
        todo.setDueDate(dto.dueDate());
        todo.setUrgency(dto.urgency() == null ? Urgency.MEDIUM : dto.urgency());

        Integer rec = dto.recurrenceDays();
        if (rec != null && rec > 0) {
            todo.setRecurrenceDays(rec);
        } else {
            todo.setRecurrenceDays(null);
        }

        Todo saved = todoRepository.save(todo);
        return toResponse(saved);
    }

    public TodoResponse updateTodo(Long id, UpdateTodoDto dto) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Todo", id)));

        if (todo.isArchived()) {
            throw new NotFoundException(new NotFoundError("Todo", id));
        }

        ValidationErrors errors = new ValidationErrors();

        boolean hasAny = dto != null && (
                dto.title() != null ||
                dto.completed() != null ||
                dto.categoryId() != null ||
                dto.dueDate() != null ||
                dto.urgency() != null ||
                dto.recurrenceDays() != null
        );

        if (!hasAny) {
            errors.addError("body", "At least one field must be provided");
            throw BadRequestException.from(errors);
        }

        if (dto.title() != null) {
            if (dto.title().isBlank() || dto.title().trim().length() < 2) {
                errors.addError("title", "Title must be longer than 1 character");
            } else {
                todo.setTitle(dto.title().trim());
            }
        }

        if (dto.completed() != null) {
            todo.setCompleted(Boolean.TRUE.equals(dto.completed()));
        }

        if (dto.categoryId() != null) {
            if (dto.categoryId() <= 0) {
                errors.addError("categoryId", "Category ID must be positive");
            } else {
                Category category = categoryRepository.findById(dto.categoryId())
                        .orElseThrow(() -> new NotFoundException(new NotFoundError("Category", dto.categoryId())));
                todo.setCategory(category);
            }
        }

        if (dto.dueDate() != null) {
            todo.setDueDate(dto.dueDate());
        }

        if (dto.urgency() != null) {
            todo.setUrgency(dto.urgency());
        }

        if (dto.recurrenceDays() != null) {
            if (dto.recurrenceDays() < 0) {
                errors.addError("recurrenceDays", "Recurrence days must be 0 or positive");
            } else if (dto.recurrenceDays() == 0) {
                todo.setRecurrenceDays(null);
            } else {
                todo.setRecurrenceDays(dto.recurrenceDays());
            }
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        Todo saved = todoRepository.save(todo);
        return toResponse(saved);
    }

    public void archiveTodo(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Todo", id)));

        if (todo.isArchived()) {
            return;
        }

        todo.setArchived(true);
        todoRepository.save(todo);
    }

    public TodoResponse duplicateTodo(Long id, Integer shiftDays) {
        Todo original = todoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Todo", id)));

        if (original.isArchived()) {
            throw new NotFoundException(new NotFoundError("Todo", id));
        }

        int shift = 0;
        if (shiftDays != null && shiftDays > 0) {
            shift = shiftDays;
        } else if (original.getRecurrenceDays() != null && original.getRecurrenceDays() > 0) {
            shift = original.getRecurrenceDays();
        }

        Todo copy = new Todo();
        copy.setTitle(original.getTitle());
        copy.setCompleted(false);
        copy.setArchived(false);
        copy.setCategory(original.getCategory());
        copy.setUrgency(original.getUrgency() == null ? Urgency.MEDIUM : original.getUrgency());
        copy.setRecurrenceDays(original.getRecurrenceDays());

        if (original.getDueDate() != null && shift > 0) {
            copy.setDueDate(original.getDueDate().plusDays(shift));
        } else {
            copy.setDueDate(original.getDueDate());
        }

        Todo saved = todoRepository.save(copy);
        return toResponse(saved);
    }

    private Sort buildSort(TodoQueryParams params) {
        SortOrder order = params == null ? SortOrder.DESC : params.orderOrDefault();

        String sortBy = (params == null || params.sortBy() == null || params.sortBy().isBlank())
                ? "createdAt"
                : params.sortBy().trim();

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "createdAt";
        }

        return order == SortOrder.ASC
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
    }

    private Specification<Todo> buildSpec(TodoQueryParams params, Long categoryId) {
        Specification<Todo> spec = (root, query, cb) -> cb.isFalse(root.get("isArchived"));

        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }

        if (params == null) {
            return spec;
        }

        if (params.completed() != null) {
            boolean completed = Boolean.TRUE.equals(params.completed());
            spec = spec.and((root, query, cb) -> completed ? cb.isTrue(root.get("completed")) : cb.isFalse(root.get("completed")));
        }

        if (params.urgency() != null) {
            Urgency u = params.urgency();
            spec = spec.and((root, query, cb) -> cb.equal(root.get("urgency"), u));
        }

        if (params.dueBefore() != null) {
            LocalDate d = params.dueBefore();
            spec = spec.and((root, query, cb) -> cb.and(
                    cb.isNotNull(root.get("dueDate")),
                    cb.lessThanOrEqualTo(root.get("dueDate"), d)
            ));
        }

        if (params.dueAfter() != null) {
            LocalDate d = params.dueAfter();
            spec = spec.and((root, query, cb) -> cb.and(
                    cb.isNotNull(root.get("dueDate")),
                    cb.greaterThanOrEqualTo(root.get("dueDate"), d)
            ));
        }

        if (Boolean.TRUE.equals(params.overdue())) {
            LocalDate today = LocalDate.now(ZoneOffset.UTC);
            spec = spec.and((root, query, cb) -> cb.and(
                    cb.isNotNull(root.get("dueDate")),
                    cb.lessThan(root.get("dueDate"), today),
                    cb.isFalse(root.get("completed"))
            ));
        }

        return spec;
    }

    private TodoResponse toResponse(Todo todo) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        boolean overdue = todo.getDueDate() != null && !todo.isCompleted() && todo.getDueDate().isBefore(today);

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.isCompleted(),
                todo.isArchived(),
                todo.getDueDate(),
                todo.getUrgency(),
                todo.getRecurrenceDays(),
                overdue,
                todo.getCategory() == null ? null : new CategoryResponse(
                        todo.getCategory().getId(),
                        todo.getCategory().getName()
                )
        );
    }
}