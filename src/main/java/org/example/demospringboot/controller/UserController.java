package org.example.demospringboot.controller;

import jakarta.validation.Valid;
import org.example.demospringboot.dto.CreateUserRequest;
import org.example.demospringboot.dto.UpdateUserRequest;
import org.example.demospringboot.entity.User;
import org.example.demospringboot.exception.ApiError;
import org.example.demospringboot.exception.UserNotFound;
import org.example.demospringboot.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers(
            @RequestParam(required = false, defaultValue = "id,asc") String sort) {
        return userService.getAllUsers(sort);
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id).orElseThrow(() -> new UserNotFound(id));
    }

    @PostMapping
    public User createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        return userService.createUser(createUserRequest);
    }

    @PutMapping("{id}")
    public User updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return userService.updateUser(id, updateUserRequest);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

//    @ExceptionHandler(UserNotFound.class)
//    public ResponseEntity<ApiError> exceptionHandlerUserNotFound(Exception e) {
//        ApiError error = new ApiError(
//                Instant.now(),
//                HttpStatus.NOT_FOUND.value(),
//                e.getMessage()
//        );
//        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//    }
}
