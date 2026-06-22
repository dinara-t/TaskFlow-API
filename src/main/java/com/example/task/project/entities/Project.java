package com.example.task.project.entities;

import java.time.LocalDateTime;

import com.example.task.common.TimeStampEntityListener;
import com.example.task.common.entity.BaseEntity;
import com.example.task.common.entity.traits.Timestampable;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "projects", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@EntityListeners(TimeStampEntityListener.class)
public class Project extends BaseEntity implements Timestampable {

    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Project() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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