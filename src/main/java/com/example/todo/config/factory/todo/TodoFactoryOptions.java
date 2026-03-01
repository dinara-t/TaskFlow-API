package com.example.todo.config.factory.todo;

import java.time.LocalDate;

import com.example.todo.category.entities.Category;
import com.example.todo.todo.entities.Urgency;

public class TodoFactoryOptions {
    private String title;
    private Boolean completed;
    private Boolean isArchived;
    private Category category;
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public Integer getRecurrenceDays() {
        return recurrenceDays;
    }

    public TodoFactoryOptions title(String title) {
        this.title = title;
        return this;
    }

    public TodoFactoryOptions completed(Boolean completed) {
        this.completed = completed;
        return this;
    }

    public TodoFactoryOptions archived(Boolean isArchived) {
        this.isArchived = isArchived;
        return this;
    }

    public TodoFactoryOptions category(Category category) {
        this.category = category;
        return this;
    }

    public TodoFactoryOptions dueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public TodoFactoryOptions urgency(Urgency urgency) {
        this.urgency = urgency;
        return this;
    }

    public TodoFactoryOptions recurrenceDays(Integer recurrenceDays) {
        this.recurrenceDays = recurrenceDays;
        return this;
    }
}