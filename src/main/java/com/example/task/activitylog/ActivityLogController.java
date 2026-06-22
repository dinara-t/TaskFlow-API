package com.example.task.activitylog;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.task.activitylog.dtos.ActivityLogResponse;
import com.example.task.common.dto.PageResponse;

@RestController
@RequestMapping("/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping
    public List<ActivityLogResponse> getRecentLogs() {
        return activityLogService.getRecentLogs();
    }

    @GetMapping("/paged")
    public PageResponse<ActivityLogResponse> getRecentLogsPaged(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        return activityLogService.getRecentLogsPaged(page, size);
    }
}