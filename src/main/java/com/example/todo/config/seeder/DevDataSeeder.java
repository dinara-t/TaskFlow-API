package com.example.todo.config.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.todo.config.factory.category.CategoryFactory;
import com.example.todo.config.factory.todo.TodoFactory;

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private final CategoryFactory categoryFactory;
    private final TodoFactory todoFactory;

    public DevDataSeeder(CategoryFactory categoryFactory, TodoFactory todoFactory) {
        this.categoryFactory = categoryFactory;
        this.todoFactory = todoFactory;
    }

    @Override
    public void run(String... args) {
        if (categoryFactory.repoEmpty()) {
            for (int i = 0; i < 10; i++) {
                categoryFactory.create();
            }
        }

        if (todoFactory.repoEmpty()) {
            for (int i = 0; i < 100; i++) {
                todoFactory.create();
            }
        }
    }
}