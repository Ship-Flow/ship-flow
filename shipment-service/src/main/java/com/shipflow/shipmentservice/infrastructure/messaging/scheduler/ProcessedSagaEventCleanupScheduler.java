package com.shipflow.shipmentservice.infrastructure.messaging.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.shipmentservice.infrastructure.persistence.ProcessedSagaEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessedSagaEventCleanupScheduler {

	private final ProcessedSagaEventRepository processedSagaEventRepository;

	@Scheduled(cron = "0 0 * * * *")
	@Transactional
	public void cleanUpExpiredEvents() {
		LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
		processedSagaEventRepository.deleteByProcessedAtBefore(cutoff);
		log.info("[Idempotent] Cleanup expired saga events | cutoff={}", cutoff);
	}
}
