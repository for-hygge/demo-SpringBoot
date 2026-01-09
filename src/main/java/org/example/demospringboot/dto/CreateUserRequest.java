package org.example.demospringboot.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateUserRequest {
    @NotBlank(message = "name cannot be blank")
    @Size(max = 20, message = "name cannot be longer than 20 characters")
    private String name;

    @NotBlank(message = "email cannot be blank")
    @Email(message = "wrong email format")
    private String email;

    @Min(value = 0, message = "age must be >= 0")
    @Max(value = 120, message = "age must be <= 120")
    private Integer age;

    @NotNull(message = "salary cannot be null")
    @PositiveOrZero(message = "salary must be >= 0")
    private BigDecimal salary;
}
