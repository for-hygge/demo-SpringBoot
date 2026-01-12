package org.example.demospringboot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailService {

    public void sendWelcomeEmail(String email) {
        try {
            log.info("Sending welcome email to {}", email);
            Thread.sleep(500);
            log.info("Welcome email sent to {}", email);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Mail sending interrupted for {}", email, e);
        }
    }
}