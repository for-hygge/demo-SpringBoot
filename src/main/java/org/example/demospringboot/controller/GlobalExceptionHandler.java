package org.example.demospringboot.controller;

import org.example.demospringboot.exception.ApiError;
import org.example.demospringboot.exception.CustomBadRequestException;
import org.example.demospringboot.exception.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
//@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<ApiError> exceptionHandlerUserNotFound(Exception e) {
        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
                );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CustomBadRequestException.class)
    public ResponseEntity<ApiError> exceptionHandlerCustomBadRequest(Exception e) {
        ApiError error = new ApiError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        );
        return ResponseEntity.badRequest().body(error);
    }
}
