package org.example.demospringboot.utils;

import org.example.demospringboot.exception.CustomBadRequestException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class Helper {

    public Sort buildSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "id");
        }

        String[] parts = sort.split(",");
        String sortBy = parts[0].trim();
        String order = parts.length > 1 ? parts[1].trim() : "asc";

        if (!isAllowed(sortBy)) {
            throw new CustomBadRequestException("invalid sortBy: " + sortBy);
        }

        Sort.Direction dir;
        try {
            dir = Sort.Direction.fromString(order);
        } catch (IllegalArgumentException e) {
            throw new CustomBadRequestException("invalid order: " + order);
        }

        return Sort.by(dir, sortBy);
    }

    private boolean isAllowed(String sortBy) {
        return "id".equals(sortBy)
                || "name".equals(sortBy)
                || "age".equals(sortBy)
                || "salary".equals(sortBy);
    }
}
