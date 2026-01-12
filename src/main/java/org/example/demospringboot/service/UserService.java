package org.example.demospringboot.service;

import lombok.extern.slf4j.Slf4j;
import org.example.demospringboot.dto.CreateUserRequest;
import org.example.demospringboot.dto.UpdateUserRequest;
import org.example.demospringboot.event.UserChangedEvent;
import org.example.demospringboot.exception.CustomBadRequestException;
import org.example.demospringboot.exception.UserNotFound;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
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
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final Helper helper;
    private final ApplicationEventPublisher publisher;

    public UserService(UserRepository userRepository, Helper helper, ApplicationEventPublisher publisher) {
        this.userRepository = userRepository;
        this.helper = helper;
        this.publisher = publisher;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "usersList", key = "#sort == null || #sort.isBlank() ? 'id,asc' : #sort")
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
    @Cacheable(cacheNames = "users", key = "#id", unless = "#result.isEmpty()")
    public Optional<User> getUserById(Long id) {
        log.info("DB hit: getUserById({})", id);
        return userRepository.findById(id);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @CachePut(cacheNames = "users", key = "#result.id")
    public User createUser(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setName(createUserRequest.getName());
        user.setEmail(createUserRequest.getEmail());
        user.setAge(createUserRequest.getAge());
        user.setSalary(createUserRequest.getSalary());
        try {
            User saved = userRepository.save(user);
            publisher.publishEvent(new UserChangedEvent(UserChangedEvent.Type.CREATED, saved.getId(), saved.getEmail()));
            log.info("Created one user with email: {}", saved.getEmail());
            return saved;
        } catch (DataIntegrityViolationException e) {
            throw new CustomBadRequestException("This email already exists: " + createUserRequest.getEmail());
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @CachePut(cacheNames = "users", key = "#result.id")
    public User updateUser(Long id, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound(id));

        if (updateUserRequest.getName() != null) user.setName(updateUserRequest.getName());
        if (updateUserRequest.getEmail() != null) user.setEmail(updateUserRequest.getEmail());
        if (updateUserRequest.getAge() != null) user.setAge(updateUserRequest.getAge());
        if (updateUserRequest.getSalary() != null) user.setSalary(updateUserRequest.getSalary());
        try {
            User saved = userRepository.save(user);
            publisher.publishEvent(new UserChangedEvent(UserChangedEvent.Type.UPDATED, saved.getId(), saved.getEmail()));
            return saved;
        } catch (DataIntegrityViolationException e) {
            throw new CustomBadRequestException("This email already exists: " + updateUserRequest.getEmail());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @CacheEvict(cacheNames = "users", key = "#id")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFound(id);
        }
        userRepository.deleteById(id);
        publisher.publishEvent(new UserChangedEvent(UserChangedEvent.Type.DELETED, id, null));
    }
}
