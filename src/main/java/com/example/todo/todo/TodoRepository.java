package com.example.todo.todo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.todo.todo.entities.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByIsArchivedFalse(Sort sort);

    List<Todo> findByCategoryIdAndIsArchivedFalse(Long categoryId, Sort sort);

    Optional<Todo> findByIdAndIsArchivedFalse(Long id);

    @Query("SELECT COALESCE(MAX(t.id), 0) FROM Todo t")
    Long getMaxId();
}