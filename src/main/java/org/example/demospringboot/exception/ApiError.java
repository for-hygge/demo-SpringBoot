package org.example.demospringboot.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private Instant timeStamp;
    private int status;
    private String message;
}
