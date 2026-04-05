package com.shipflow.orderservice.infrastructure.messaging.scheduler;

import com.shipflow.orderservice.infrastructure.persistence.ProcessedSagaEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessedSagaEventCleanupScheduler {

    private final ProcessedSagaEventRepository processedSagaEventRepository;

    @Scheduled(cron = "0 0 * * * *")  // 매시간 정각
    @Transactional
    public void cleanUpExpiredEvents() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        processedSagaEventRepository.deleteByProcessedAtBefore(cutoff);
        log.info("[Idempotent] Cleanup expired saga events | cutoff={}", cutoff);
    }
}
