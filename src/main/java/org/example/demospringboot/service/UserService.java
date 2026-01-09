package org.example.demospringboot.service;

import org.example.demospringboot.dto.CreateUserRequest;
import org.example.demospringboot.dto.UpdateUserRequest;
import org.example.demospringboot.exception.CustomBadRequestException;
import org.example.demospringboot.exception.UserNotFound;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.example.demospringboot.entity.User;
import org.example.demospringboot.repository.UserRepository;
import org.example.demospringboot.utils.Helper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public final Helper helper;

    public UserService(UserRepository userRepository, Helper helper) {
        this.userRepository = userRepository;
        this.helper = helper;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers(String sort) {
        List<User> users = userRepository.findAll();
        if (sort == null || sort.isBlank()) {
            return users;
        }
        String[] parts = sort.split(",");
        String sortBy = parts[0];
        String order = parts.length > 1 ? parts[1] : "asc";

        Comparator<User> comparator = helper.buildUserComparator(sortBy);
        if ("desc".equalsIgnoreCase(order)) {
                comparator = comparator.reversed();
        }

        users.sort(comparator);
        return users;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public User createUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new CustomBadRequestException("This email already exists: " + createUserRequest.getEmail());
        }

        User user = new User();
        user.setName(createUserRequest.getName());
        user.setEmail(createUserRequest.getEmail());
        user.setAge(createUserRequest.getAge());
        user.setSalary(createUserRequest.getSalary());
        return userRepository.save(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public User updateUser(Long id, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound(id));

        if (updateUserRequest.getName() != null) user.setName(updateUserRequest.getName());
        if (updateUserRequest.getEmail() != null) {
            if (userRepository.existsByEmailAndIdNot(updateUserRequest.getEmail(), id)) {
                throw new CustomBadRequestException("This email already exists: " + updateUserRequest.getEmail());
            }
            user.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.getAge() != null) user.setAge(updateUserRequest.getAge());
        if (updateUserRequest.getSalary() != null) user.setSalary(updateUserRequest.getSalary());
        return userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFound(id);
        }
        userRepository.deleteById(id);
    }
}
