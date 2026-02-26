package com.example.todo.todo.dtos;

import com.example.todo.common.dto.SortOrder;

public record TodoQueryParams (
       String sortBy,
    SortOrder order
) {
    public SortOrder orderOrDefault() {
        return order == null ? SortOrder.DESC : order;
    }
}
