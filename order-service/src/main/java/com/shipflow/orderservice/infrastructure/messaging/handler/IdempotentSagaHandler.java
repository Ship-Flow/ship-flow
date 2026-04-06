package com.shipflow.orderservice.infrastructure.messaging.handler;

import com.shipflow.common.messaging.event.SagaEvent;
import com.shipflow.common.messaging.handler.AbstractSagaHandler;
import com.shipflow.orderservice.infrastructure.persistence.ProcessedSagaEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Slf4j
public abstract class IdempotentSagaHandler<T extends SagaEvent> extends AbstractSagaHandler<T> {

    private static final String REDIS_KEY_PREFIX = "idempotent:saga:";

    @Autowired
    private ProcessedSagaEventRepository processedSagaEventRepository;

    @Autowired
    private IdempotentSagaExecutor sagaExecutor;

    @Override
    protected final void process(T event) {
        String redisKey = REDIS_KEY_PREFIX + event.getEventId();

        // [1] Redis에서 중복 확인
        if (sagaExecutor.hasProcessed(redisKey)) {
            log.warn("[Idempotent] Duplicate event skipped | eventId={} | eventType={} | detectedAt={}",
                    event.getEventId(), event.getEventType(), LocalDateTime.now());
            return;
        }

        // [2] DB에서 중복 확인 (Redis 재시작 후 복구용)
        if (processedSagaEventRepository.existsById(event.getEventId())) {
            log.warn("[Idempotent] Duplicate event skipped | eventId={} | eventType={} | detectedAt={}",
                    event.getEventId(), event.getEventType(), LocalDateTime.now());
            // Redis 재워밍 (TTL 재설정)
            sagaExecutor.rewarmCache(redisKey);
            return;
        }

        // [3] 신규 이벤트 처리: 별도 컴포넌트에서 트랜잭션 적용
        sagaExecutor.executeWithIdempotency(event, redisKey, this::doProcess);
    }

    protected abstract void doProcess(T event);
}
