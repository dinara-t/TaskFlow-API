package com.example.todo.todo;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.todo.todo.dtos.CreateTodoDto;
import com.example.todo.todo.dtos.TodoQueryParams;
import com.example.todo.todo.dtos.TodoResponse;
import com.example.todo.todo.dtos.UpdateTodoDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoResponse> getTodos(
            @ModelAttribute TodoQueryParams params,
            @RequestParam(required = false) Long category
    ) {
        return todoService.getTodos(params, category);
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody CreateTodoDto dto) {
        TodoResponse created = todoService.createTodo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public TodoResponse updateTodo(@PathVariable Long id, @RequestBody UpdateTodoDto dto) {
        return todoService.updateTodo(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.archiveTodo(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/duplicate")
    public ResponseEntity<TodoResponse> duplicateTodo(
            @PathVariable Long id,
            @RequestParam(required = false) Integer shiftDays
    ) {
        TodoResponse created = todoService.duplicateTodo(id, shiftDays);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}