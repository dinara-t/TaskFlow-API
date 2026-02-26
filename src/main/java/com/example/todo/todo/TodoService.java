package com.example.todo.todo;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.todo.category.CategoryRepository;
import com.example.todo.category.dtos.CategoryResponse;
import com.example.todo.category.entities.Category;
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

@Service
public class TodoService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "title", "completed");

    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;

    public TodoService(TodoRepository todoRepository, CategoryRepository categoryRepository) {
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<TodoResponse> getTodos(TodoQueryParams params, Long categoryId) {
        SortOrder order = params == null ? SortOrder.DESC : params.orderOrDefault();

        String sortBy = (params == null || params.sortBy() == null || params.sortBy().isBlank())
                ? "createdAt"
                : params.sortBy().trim();

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "createdAt";
        }

        Sort sort = order == SortOrder.ASC
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        List<Todo> todos = categoryId != null
                ? todoRepository.findByCategoryIdAndIsArchivedFalse(categoryId, sort)
                : todoRepository.findByIsArchivedFalse(sort);

        return todos.stream().map(this::toResponse).toList();
    }

    public TodoResponse createTodo(CreateTodoDto dto) {
        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Category", dto.categoryId())));

        Todo todo = new Todo();
        todo.setTitle(dto.title().trim());
        todo.setCompleted(Boolean.TRUE.equals(dto.completed()));
        todo.setArchived(false);
        todo.setCategory(category);

        Todo saved = todoRepository.save(todo);
        return toResponse(saved);
    }

    public TodoResponse updateTodo(Long id, UpdateTodoDto dto) {
        Todo todo = todoRepository.findByIdAndIsArchivedFalse(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Todo", id)));

        ValidationErrors errors = new ValidationErrors();

        boolean hasAny = dto != null && (dto.title() != null || dto.completed() != null || dto.categoryId() != null);
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

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        Todo saved = todoRepository.save(todo);
        return toResponse(saved);
    }

    public void archiveTodo(Long id) {
        Todo todo = todoRepository.findByIdAndIsArchivedFalse(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Todo", id)));
        todo.setArchived(true);
        todoRepository.save(todo);
    }

    private TodoResponse toResponse(Todo todo) {
        if (todo == null) {
            return null;
        }
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.isCompleted(),
                todo.getCategory() == null ? null : new CategoryResponse(
                        todo.getCategory().getId(),
                        todo.getCategory().getName()
                )
        );
    }
}