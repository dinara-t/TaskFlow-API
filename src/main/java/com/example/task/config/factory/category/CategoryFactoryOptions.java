package com.example.task.config.factory.category;

public class CategoryFactoryOptions {
    private String name;

    public String getName() {
        return name;
    }

    public CategoryFactoryOptions name(String name) {
        this.name = name;
        return this;
    }
}