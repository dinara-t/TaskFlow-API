package com.example.task.comment;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.task.comment.dtos.CommentResponse;
import com.example.task.comment.dtos.CreateCommentDto;
import com.example.task.comment.dtos.UpdateCommentDto;

import jakarta.validation.Valid;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/tasks/{taskId}/comments")
    public List<CommentResponse> getCommentsForTask(@PathVariable Long taskId) {
        return commentService.getCommentsForTask(taskId);
    }

    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentDto dto
    ) {
        CommentResponse created = commentService.createComment(taskId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/comments/{id}")
    public CommentResponse updateComment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCommentDto dto
    ) {
        return commentService.updateComment(id, dto);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}