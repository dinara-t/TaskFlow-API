package com.example.task.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.task.user.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}