package com.shipflow.shipmentservice.infrastructure.messaging.handler;

import java.time.Duration;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.shipflow.common.messaging.event.SagaEvent;
import com.shipflow.shipmentservice.application.client.CacheClient;
import com.shipflow.shipmentservice.infrastructure.persistence.ProcessedSagaEventJpaEntity;
import com.shipflow.shipmentservice.infrastructure.persistence.ProcessedSagaEventRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IdempotentSagaExecutor {

	private static final Duration REDIS_TTL = Duration.ofHours(24);

	private final ProcessedSagaEventRepository processedSagaEventRepository;
	private final CacheClient cacheClient;

	public boolean hasProcessed(String redisKey) {
		return cacheClient.hasKey(redisKey);
	}

	public void rewarmCache(String redisKey) {
		cacheClient.set(redisKey, "1", REDIS_TTL);
	}

	@Transactional
	public <T extends SagaEvent> void executeWithIdempotency(T event, String redisKey, Consumer<T> doProcess) {
		doProcess.accept(event);

		processedSagaEventRepository.save(
			ProcessedSagaEventJpaEntity.of(event.getEventId(), event.getEventType())
		);

		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				cacheClient.set(redisKey, "1", REDIS_TTL);
			}
		});
	}
}
