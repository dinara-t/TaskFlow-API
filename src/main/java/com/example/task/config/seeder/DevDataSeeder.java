package com.example.task.config.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.task.config.factory.category.CategoryFactory;
import com.example.task.config.factory.task.TaskFactory;

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private final CategoryFactory categoryFactory;
    private final TaskFactory taskFactory;

    public DevDataSeeder(CategoryFactory categoryFactory, TaskFactory taskFactory) {
        this.categoryFactory = categoryFactory;
        this.taskFactory = taskFactory;
    }

    @Override
    public void run(String... args) {
        if (categoryFactory.repoEmpty()) {
            for (int i = 0; i < 10; i++) {
                categoryFactory.create();
            }
        }

        if (taskFactory.repoEmpty()) {
            for (int i = 0; i < 100; i++) {
                taskFactory.create();
            }
        }
    }
}