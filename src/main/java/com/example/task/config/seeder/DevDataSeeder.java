package com.example.task.config.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.task.config.factory.category.CategoryFactory;
import com.example.task.config.factory.task.TaskFactory;
import com.example.task.user.UserRepository;
import com.example.task.user.entities.Role;
import com.example.task.user.entities.User;

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private final CategoryFactory categoryFactory;
    private final TaskFactory taskFactory;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataSeeder(
            CategoryFactory categoryFactory,
            TaskFactory taskFactory,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.categoryFactory = categoryFactory;
        this.taskFactory = taskFactory;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();

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

    private void seedUsers() {
        createUserIfMissing(
                "Admin User",
                "admin@taskflow.com",
                "password123",
                Role.ADMIN
        );

        createUserIfMissing(
                "Project Manager",
                "manager@taskflow.com",
                "password123",
                Role.MANAGER
        );

        createUserIfMissing(
                "Team Member One",
                "member1@taskflow.com",
                "password123",
                Role.MEMBER
        );

        createUserIfMissing(
                "Team Member Two",
                "member2@taskflow.com",
                "password123",
                Role.MEMBER
        );

        createUserIfMissing(
                "Demo Client",
                "client@taskflow.com",
                "password123",
                Role.MEMBER
        );
    }

    private void createUserIfMissing(
            String name,
            String email,
            String password,
            Role role
    ) {
        String cleanEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(cleanEmail)) {
            return;
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(cleanEmail);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);

        userRepository.save(user);
    }
}