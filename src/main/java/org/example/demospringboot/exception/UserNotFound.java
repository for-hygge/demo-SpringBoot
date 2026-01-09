package org.example.demospringboot.exception;

public class UserNotFound extends RuntimeException{
    public UserNotFound(Long id) {
        super("User Not Found: userId= " + id);
    }
}
