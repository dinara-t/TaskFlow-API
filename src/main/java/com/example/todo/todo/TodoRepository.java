package com.example.todo.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.example.todo.todo.entities.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {
    @Query("SELECT COALESCE(MAX(t.id), 0) FROM Todo t")
    Long getMaxId();
}