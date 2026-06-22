package com.example.task.comment;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.task.activitylog.ActivityLogService;
import com.example.task.activitylog.entities.ActivityAction;
import com.example.task.activitylog.entities.ActivityEntityType;
import com.example.task.comment.dtos.CommentResponse;
import com.example.task.comment.dtos.CreateCommentDto;
import com.example.task.comment.dtos.UpdateCommentDto;
import com.example.task.comment.entities.Comment;
import com.example.task.common.exception.BadRequestException;
import com.example.task.common.exception.NotFoundException;
import com.example.task.common.serviceErrors.NotFoundError;
import com.example.task.common.serviceErrors.ValidationErrors;
import com.example.task.task.TaskRepository;
import com.example.task.task.entities.Task;
import com.example.task.user.UserRepository;
import com.example.task.user.entities.User;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public CommentService(
            CommentRepository commentRepository,
            TaskRepository taskRepository,
            UserRepository userRepository,
            ActivityLogService activityLogService
    ) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    public List<CommentResponse> getCommentsForTask(Long taskId) {
        ensureTaskExists(taskId);

        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }

    public CommentResponse createComment(Long taskId, CreateCommentDto dto) {
        ValidationErrors errors = new ValidationErrors();

        if (dto == null || dto.body() == null || dto.body().isBlank()) {
            errors.addError("body", "Comment body must not be blank");
        }

        if (dto != null && dto.body() != null && dto.body().trim().length() > 1000) {
            errors.addError("body", "Comment body must be 1000 characters or less");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Task", taskId)));

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setBody(dto.body().trim());
        comment.setUser(getCurrentUserOrNull());

        Comment saved = commentRepository.save(comment);

        activityLogService.log(
                ActivityAction.COMMENT_CREATED,
                ActivityEntityType.COMMENT,
                saved.getId(),
                "Comment added to task #" + task.getId()
        );

        return CommentResponse.from(saved);
    }

    public CommentResponse updateComment(Long id, UpdateCommentDto dto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Comment", id)));

        ValidationErrors errors = new ValidationErrors();

        if (dto == null || dto.body() == null || dto.body().isBlank()) {
            errors.addError("body", "Comment body must not be blank");
        }

        if (dto != null && dto.body() != null && dto.body().trim().length() > 1000) {
            errors.addError("body", "Comment body must be 1000 characters or less");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        comment.setBody(dto.body().trim());

        Comment saved = commentRepository.save(comment);

        activityLogService.log(
                ActivityAction.COMMENT_UPDATED,
                ActivityEntityType.COMMENT,
                saved.getId(),
                "Comment updated on task #" + saved.getTask().getId()
        );

        return CommentResponse.from(saved);
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new NotFoundError("Comment", id)));

        Long taskId = comment.getTask().getId();

        commentRepository.delete(comment);

        activityLogService.log(
                ActivityAction.COMMENT_DELETED,
                ActivityEntityType.COMMENT,
                id,
                "Comment deleted from task #" + taskId
        );
    }

    private void ensureTaskExists(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new NotFoundException(new NotFoundError("Task", taskId));
        }
    }

    private User getCurrentUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            return null;
        }

        String email = authentication.getName();

        if ("anonymousUser".equals(email)) {
            return null;
        }

        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }
}