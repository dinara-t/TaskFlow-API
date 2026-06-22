package com.example.task.activitylog;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.task.activitylog.entities.ActivityLog;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}