package com.example.todo.config.factory.todo;

import com.example.todo.category.entities.Category;

public class TodoFactoryOptions {
    private String title;
    private Boolean completed;
    private Boolean isArchived;
    private Category category;

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
}