package com.example.task.task.dtos;

import java.time.LocalDate;

import com.example.task.common.dto.SortOrder;
import com.example.task.task.entities.Urgency;

public record TaskQueryParams(
        String sortBy,
        SortOrder order,
        Boolean overdue,
        LocalDate dueBefore,
        LocalDate dueAfter,
        Urgency urgency,
        Boolean completed
) {
    public SortOrder orderOrDefault() {
        return order == null ? SortOrder.DESC : order;
    }
}