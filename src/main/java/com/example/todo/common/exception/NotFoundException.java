package com.example.todo.common.exception;

import org.springframework.http.HttpStatus;

import com.example.todo.common.serviceErrors.NotFoundError;

public class NotFoundException extends HTTPException {

    public NotFoundException(NotFoundError error) {
        super(error.getMessage(), HttpStatus.NOT_FOUND, error.getErrorType());
        this.details = null;

    }

}
