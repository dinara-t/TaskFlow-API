package com.example.task.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.task.auth.dtos.AuthResponse;
import com.example.task.auth.dtos.LoginRequest;
import com.example.task.auth.dtos.RegisterRequest;
import com.example.task.auth.dtos.UserResponse;
import com.example.task.common.exception.BadRequestException;
import com.example.task.common.exception.NotFoundException;
import com.example.task.common.serviceErrors.NotFoundError;
import com.example.task.common.serviceErrors.ValidationErrors;
import com.example.task.security.JwtService;
import com.example.task.user.UserRepository;
import com.example.task.user.entities.Role;
import com.example.task.user.entities.User;
import com.example.task.activitylog.ActivityLogService;
import com.example.task.activitylog.entities.ActivityAction;
import com.example.task.activitylog.entities.ActivityEntityType;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ActivityLogService activityLogService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            ActivityLogService activityLogService

    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.activityLogService = activityLogService;
    }

    public AuthResponse register(RegisterRequest request) {
        ValidationErrors errors = new ValidationErrors();

        if (request == null) {
            errors.addError("request", "Request body is required");
            throw BadRequestException.from(errors);
        }

        String name = request.name() == null ? null : request.name().trim();
        String email = request.email() == null ? null : request.email().trim().toLowerCase();
        String password = request.password();

        if (email != null && userRepository.existsByEmailIgnoreCase(email)) {
            errors.addError("email", "Email already exists");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(Role.MEMBER);

        User saved = userRepository.save(user);
     activityLogService.log(
        ActivityAction.USER_REGISTERED,
        ActivityEntityType.USER,
        saved.getId(),
        "User registered: " + saved.getEmail()
);
        return new AuthResponse(
                jwtService.generateToken(saved),
                UserResponse.from(saved)
        );
   
    }

    public AuthResponse login(LoginRequest request) {
        ValidationErrors errors = new ValidationErrors();

        if (request == null) {
            errors.addError("request", "Request body is required");
            throw BadRequestException.from(errors);
        }

        String email = request.email() == null ? null : request.email().trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> {
                    ValidationErrors loginErrors = new ValidationErrors();
                    loginErrors.addError("email", "Invalid email or password");
                    return BadRequestException.from(loginErrors);
                });

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            errors.addError("password", "Invalid email or password");
            throw BadRequestException.from(errors);
        }

        return new AuthResponse(
                jwtService.generateToken(user),
                UserResponse.from(user)
        );
    }
}