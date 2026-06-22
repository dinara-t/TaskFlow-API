package com.example.task.common;

import java.time.LocalDateTime;

import com.example.task.common.entity.traits.Timestampable;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class TimeStampEntityListener {
    @PrePersist
    public void onPrePersist(Object entity) {
        if (entity instanceof Timestampable timestampable) {
            LocalDateTime now = LocalDateTime.now();
            timestampable.setCreatedAt(now);
            timestampable.setUpdatedAt(now);
        }
    }

    @PreUpdate
    public void onPreUpdate(Object entity) {
        if (entity instanceof Timestampable timestampable) {
            timestampable.setUpdatedAt(LocalDateTime.now());
        }
    }
}
