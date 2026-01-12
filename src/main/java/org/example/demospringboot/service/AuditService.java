package org.example.demospringboot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuditService {

    public void record(String action, Long userId) {
        try {
            Thread.sleep(300);
            log.info("AUDIT | time={} | action={} | userId={}",
                    LocalDateTime.now(), action, userId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Audit logging interrupted for user {}", userId, e);
        }
    }
}
