package com.shipflow.orderservice.infrastructure.messaging.handler;

import com.shipflow.common.messaging.event.SagaEvent;
import com.shipflow.orderservice.infrastructure.persistence.ProcessedSagaEventJpaEntity;
import com.shipflow.orderservice.infrastructure.persistence.ProcessedSagaEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class IdempotentSagaExecutor {

    private final ProcessedSagaEventRepository processedSagaEventRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final Duration REDIS_TTL = Duration.ofHours(24);

    public boolean hasProcessed(String redisKey) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
    }

    public void rewarmCache(String redisKey) {
        redisTemplate.opsForValue().set(redisKey, "1", REDIS_TTL);
    }

    @Transactional
    public <T extends SagaEvent> void executeWithIdempotency(T event, String redisKey, Consumer<T> doProcess) {
        doProcess.accept(event);
        processedSagaEventRepository.save(
            ProcessedSagaEventJpaEntity.of(event.getEventId(), event.getEventType())
        );
        // 커밋 후 Redis 저장 -> Redis 는 바로 저장 되기 때문에 DB 장애를 방지하고자 DB - Redis 일관성 유지
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                redisTemplate.opsForValue().set(redisKey, "1", REDIS_TTL);
            }
        });
    }
}
