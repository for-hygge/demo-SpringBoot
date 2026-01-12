package org.example.demospringboot.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.demospringboot.event.UserChangedEvent;
import org.example.demospringboot.service.AuditService;
import org.example.demospringboot.service.MailService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class UserChangedListener {

    private final MailService mailService;
    private final AuditService auditService;
    private final CacheManager cacheManager;

    public UserChangedListener(MailService mailService, AuditService auditService, CacheManager cacheManager) {
        this.mailService = mailService;
        this.auditService = auditService;
        this.cacheManager = cacheManager;
    }

    @Async("demoTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserChangedEvent e) {
        log.info("Async user event: type={}, userId={}", e.type(), e.userId());

        auditService.record(e.type().name(), e.userId());

        if (e.type() == UserChangedEvent.Type.CREATED && e.email() != null) {
            mailService.sendWelcomeEmail(e.email());
        }

        Cache usersList = cacheManager.getCache("usersList");
        if (usersList != null) {
            usersList.clear();
            log.info("Cleared cache: usersList");
        }
    }
}
