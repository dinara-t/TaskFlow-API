package com.example.task.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.task.comment.entities.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskIdOrderByCreatedAtAsc(Long taskId);
}