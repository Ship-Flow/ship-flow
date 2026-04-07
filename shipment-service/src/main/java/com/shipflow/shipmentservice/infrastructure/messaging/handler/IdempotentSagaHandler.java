package com.shipflow.shipmentservice.infrastructure.messaging.handler;

import com.shipflow.common.messaging.event.SagaEvent;
import com.shipflow.common.messaging.handler.AbstractSagaHandler;
import com.shipflow.shipmentservice.infrastructure.persistence.ProcessedSagaEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class IdempotentSagaHandler<T extends SagaEvent> extends AbstractSagaHandler<T> {

	private static final String REDIS_KEY_PREFIX = "idempotent:saga:";

	private final ProcessedSagaEventRepository processedSagaEventRepository;
	private final IdempotentSagaExecutor sagaExecutor;

	@Override
	protected final void process(T event) {
		String redisKey = REDIS_KEY_PREFIX + event.getEventId();

		if (sagaExecutor.hasProcessed(redisKey)) {
			log.info("[Idempotent] Skip - already processed (Redis) | eventId={}", event.getEventId());
			return;
		}

		if (processedSagaEventRepository.existsById(event.getEventId())) {
			log.info("[Idempotent] Skip - already processed (DB) | eventId={}", event.getEventId());
			sagaExecutor.rewarmCache(redisKey);
			return;
		}

		sagaExecutor.executeWithIdempotency(event, redisKey, this::doProcess);
	}

	protected abstract void doProcess(T event);
}
