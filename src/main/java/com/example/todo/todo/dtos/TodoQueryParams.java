package com.example.todo.todo.dtos;

import java.time.LocalDate;

import com.example.todo.common.dto.SortOrder;
import com.example.todo.todo.entities.Urgency;

public record TodoQueryParams(
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