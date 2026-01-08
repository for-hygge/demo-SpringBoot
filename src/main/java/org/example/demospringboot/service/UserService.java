package org.example.demospringboot.service;

import org.example.demospringboot.entity.User;
import org.example.demospringboot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("This user doesn't exist, userId is " + id)
        );
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User updatedUser = getUserById(id);
        updatedUser.setAge(user.getAge());
        updatedUser.setName(user.getName());
        updatedUser.setSalary(user.getSalary());
        return userRepository.save(updatedUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
