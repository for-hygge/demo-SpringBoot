package org.example.demospringboot.utils;

import org.example.demospringboot.entity.User;
import org.example.demospringboot.exception.CustomBadRequestException;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class Helper {
    public Comparator<User> buildUserComparator(String sortBy) {
        switch (sortBy) {
            case "id":
                return Comparator.comparing(User::getId);
            case "name":
                return Comparator.comparing(User::getName);
            case "age":
                return Comparator.comparing(User::getAge);
            case "salary":
                return Comparator.comparing(User::getSalary);
            default:
                throw new CustomBadRequestException("invalid sortBy" + sortBy);
        }
    }
}
