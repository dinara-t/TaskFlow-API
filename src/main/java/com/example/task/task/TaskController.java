package com.example.task.task;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.task.common.dto.PageResponse;
import com.example.task.task.dtos.CreateTaskDto;
import com.example.task.task.dtos.TaskQueryParams;
import com.example.task.task.dtos.TaskResponse;
import com.example.task.task.dtos.UpdateTaskDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskResponse> getTasks(
            @ModelAttribute TaskQueryParams params,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) Long project
    ) {
        return taskService.getTasks(params, category, project);
    }

    @GetMapping("/paged")
    public PageResponse<TaskResponse> getTasksPaged(
            @ModelAttribute TaskQueryParams params,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) Long project,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return taskService.getTasksPaged(params, category, project, page, size);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskDto dto) {
        TaskResponse created = taskService.createTask(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(@PathVariable Long id, @RequestBody UpdateTaskDto dto) {
        return taskService.updateTask(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.archiveTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/duplicate")
    public ResponseEntity<TaskResponse> duplicateTask(
            @PathVariable Long id,
            @RequestParam(required = false) Integer shiftDays
    ) {
        TaskResponse created = taskService.duplicateTask(id, shiftDays);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}