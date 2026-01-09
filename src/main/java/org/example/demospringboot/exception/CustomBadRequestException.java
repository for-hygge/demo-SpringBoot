package org.example.demospringboot.exception;

public class CustomBadRequestException extends RuntimeException {
    public CustomBadRequestException(String sorBy) {
        super(sorBy);
    }
}
