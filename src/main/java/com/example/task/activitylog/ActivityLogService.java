package com.example.task.activitylog;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.task.activitylog.dtos.ActivityLogResponse;
import com.example.task.activitylog.entities.ActivityAction;
import com.example.task.activitylog.entities.ActivityEntityType;
import com.example.task.activitylog.entities.ActivityLog;
import com.example.task.common.dto.PageResponse;
import com.example.task.common.exception.BadRequestException;
import com.example.task.common.serviceErrors.ValidationErrors;
import com.example.task.user.UserRepository;
import com.example.task.user.entities.User;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public ActivityLogService(
            ActivityLogRepository activityLogRepository,
            UserRepository userRepository
    ) {
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
    }

    public void log(
            ActivityAction action,
            ActivityEntityType entityType,
            Long entityId,
            String message
    ) {
        if (action == null || entityType == null || entityId == null || message == null || message.isBlank()) {
            return;
        }

        ActivityLog log = new ActivityLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setMessage(message.trim());
        log.setUser(getCurrentUserOrNull());

        activityLogRepository.save(log);
    }

    public List<ActivityLogResponse> getRecentLogs() {
        return activityLogRepository
                .findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(ActivityLogResponse::from)
                .toList();
    }

    public PageResponse<ActivityLogResponse> getRecentLogsPaged(Integer page, Integer size) {
        int safePage = page == null ? 0 : page;
        int safeSize = size == null ? 20 : size;

        ValidationErrors errors = new ValidationErrors();

        if (safePage < 0) {
            errors.addError("page", "Page must be 0 or greater");
        }

        if (safeSize <= 0) {
            errors.addError("size", "Size must be greater than 0");
        }

        if (safeSize > 200) {
            errors.addError("size", "Size must be 200 or less");
        }

        if (errors.hasErrors()) {
            throw BadRequestException.from(errors);
        }

        PageRequest pageable = PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<ActivityLog> logsPage = activityLogRepository.findAll(pageable);

        List<ActivityLogResponse> items = logsPage.getContent()
                .stream()
                .map(ActivityLogResponse::from)
                .toList();

        return new PageResponse<>(
                items,
                logsPage.getNumber(),
                logsPage.getSize(),
                logsPage.getTotalElements(),
                logsPage.getTotalPages(),
                logsPage.hasNext(),
                logsPage.hasPrevious()
        );
    }

    private User getCurrentUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            return null;
        }

        String email = authentication.getName();

        if ("anonymousUser".equals(email)) {
            return null;
        }

        return userRepository.findByEmailIgnoreCase(email).orElse(null);
    }
}