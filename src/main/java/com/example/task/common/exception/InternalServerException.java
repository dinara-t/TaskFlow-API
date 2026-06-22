package com.example.task.common.exception;

import org.springframework.http.HttpStatus;

import com.example.task.common.serviceErrors.ServiceError;

public class InternalServerException extends HTTPException {

    public InternalServerException(ServiceError ex) {
        super(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
        this.details = null;
    }

}
