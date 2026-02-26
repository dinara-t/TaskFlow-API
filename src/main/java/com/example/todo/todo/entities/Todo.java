package com.example.todo.todo.entities;

import java.time.LocalDateTime;

import com.example.todo.category.entities.Category;
import com.example.todo.common.TimeStampEntityListener;
import com.example.todo.common.entity.BaseEntity;
import com.example.todo.common.entity.traits.Timestampable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "todos")
@EntityListeners(TimeStampEntityListener.class)
public class Todo extends BaseEntity implements Timestampable {

    private String title;

    private boolean completed = false;

    private boolean isArchived = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnoreProperties("todos")
    private Category category;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Todo() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}