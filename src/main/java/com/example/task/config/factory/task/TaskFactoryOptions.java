package com.example.task.config.factory.task;

import java.time.LocalDate;

import com.example.task.category.entities.Category;
import com.example.task.project.entities.Project;
import com.example.task.task.entities.Urgency;

public class TaskFactoryOptions {
    private String title;
    private Boolean completed;
    private Boolean isArchived;
    private Category category;
    private Project project;
    private LocalDate dueDate;
    private Urgency urgency;
    private Integer recurrenceDays;

    public String getTitle() {
        return title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public Category getCategory() {
        return category;
    }

    public Project getProject() {
        return project;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public Integer getRecurrenceDays() {
        return recurrenceDays;
    }

    public TaskFactoryOptions title(String title) {
        this.title = title;
        return this;
    }

    public TaskFactoryOptions completed(Boolean completed) {
        this.completed = completed;
        return this;
    }

    public TaskFactoryOptions archived(Boolean isArchived) {
        this.isArchived = isArchived;
        return this;
    }

    public TaskFactoryOptions category(Category category) {
        this.category = category;
        return this;
    }

    public TaskFactoryOptions project(Project project) {
        this.project = project;
        return this;
    }

    public TaskFactoryOptions dueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public TaskFactoryOptions urgency(Urgency urgency) {
        this.urgency = urgency;
        return this;
    }

    public TaskFactoryOptions recurrenceDays(Integer recurrenceDays) {
        this.recurrenceDays = recurrenceDays;
        return this;
    }
}