package com.example.task.task.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.task.category.entities.Category;
import com.example.task.common.TimeStampEntityListener;
import com.example.task.common.entity.BaseEntity;
import com.example.task.common.entity.traits.Timestampable;
import com.example.task.project.entities.Project;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import com.example.task.comment.entities.Comment;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

@Entity
@Table(name = "tasks")
@EntityListeners(TimeStampEntityListener.class)
public class Task extends BaseEntity implements Timestampable {

    private String title;

    private boolean completed = false;

    private boolean isArchived = false;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private Urgency urgency = Urgency.MEDIUM;

    private Integer recurrenceDays;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnoreProperties("tasks")
    private Category category;
@OneToMany(mappedBy = "task", cascade = CascadeType.REMOVE, orphanRemoval = true)
private List<Comment> comments = new ArrayList<>();
    @ManyToOne(optional = true)
    @JoinColumn(name = "project_id", nullable = true)
    @JsonIgnoreProperties("tasks")
    private Project project;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Task() {
    }

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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
    }

    public Integer getRecurrenceDays() {
        return recurrenceDays;
    }

    public void setRecurrenceDays(Integer recurrenceDays) {
        this.recurrenceDays = recurrenceDays;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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
    public List<Comment> getComments() {
    return comments;
}

public void setComments(List<Comment> comments) {
    this.comments = comments;
}
}