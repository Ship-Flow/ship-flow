package com.shipflow.orderservice.infrastructure.messaging.handler;

import com.shipflow.common.messaging.event.SagaEvent;
import com.shipflow.orderservice.infrastructure.persistence.ProcessedSagaEventJpaEntity;
import com.shipflow.orderservice.infrastructure.persistence.ProcessedSagaEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdempotentSagaExecutorTest {

    // ── 테스트용 이벤트 ──────────────────────────────────────────────────────────
    static class TestEvent extends SagaEvent {
        TestEvent() {
            super("test.event");
        }
    }

    // ── Mocks ────────────────────────────────────────────────────────────────────
    @Mock
    private ProcessedSagaEventRepository processedSagaEventRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private IdempotentSagaExecutor executor;

    // ── 1. hasProcessed - Redis HIT → true 반환 ───────────────────────────────────
    @Test
    void hasProcessed_Redis_HIT_true_반환() {
        String redisKey = "idempotent:saga:some-event-id";
        when(redisTemplate.hasKey(redisKey)).thenReturn(true);

        boolean result = executor.hasProcessed(redisKey);

        assertThat(result).isTrue();
        verify(redisTemplate).hasKey(redisKey);
    }

    // ── 2. hasProcessed - Redis MISS → false 반환 ─────────────────────────────────
    @Test
    void hasProcessed_Redis_MISS_false_반환() {
        String redisKey = "idempotent:saga:some-event-id";
        when(redisTemplate.hasKey(redisKey)).thenReturn(false);

        boolean result = executor.hasProcessed(redisKey);

        assertThat(result).isFalse();
        verify(redisTemplate).hasKey(redisKey);
    }

    // ── 3. rewarmCache - opsForValue().set 호출 검증 ──────────────────────────────
    @Test
    void rewarmCache_Redis_TTL_재설정_호출() {
        String redisKey = "idempotent:saga:some-event-id";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        executor.rewarmCache(redisKey);

        verify(redisTemplate).opsForValue();
        verify(valueOperations).set(eq(redisKey), eq("1"), eq(Duration.ofHours(24)));
    }

    // ── 4. executeWithIdempotency - doProcess 호출 + DB 저장 검증 ────────────────
    @Test
    void executeWithIdempotency_doProcess_호출_및_DB_저장() {
        TestEvent event = new TestEvent();
        String redisKey = "idempotent:saga:" + event.getEventId();

        AtomicBoolean doProcessCalled = new AtomicBoolean(false);
        Consumer<TestEvent> doProcess = e -> doProcessCalled.set(true);

        // TransactionSynchronizationManager 는 실제 트랜잭션 없이 호출하면
        // IllegalStateException 이 발생하므로, executeWithIdempotency 를 직접 호출하지 않고
        // doProcess 와 repository.save 부분만 검증한다.
        // 실제 @Transactional 동작은 통합 테스트 영역이므로 여기서는
        // 트랜잭션 컨텍스트 없이 호출 가능한 범위까지만 검증한다.

        // TransactionSynchronizationManager.registerSynchronization 호출 시
        // 활성 트랜잭션이 없으면 예외 발생 → 실제 트랜잭션 없이 호출 시 예외를 허용하되
        // doProcess 및 save 는 그 이전에 실행되므로 정상 호출됨을 검증한다.
        try {
            executor.executeWithIdempotency(event, redisKey, doProcess);
        } catch (Exception ignored) {
            // TransactionSynchronizationManager 관련 예외는 단위 테스트에서 무시
        }

        assertThat(doProcessCalled.get()).isTrue();

        ArgumentCaptor<ProcessedSagaEventJpaEntity> captor =
                ArgumentCaptor.forClass(ProcessedSagaEventJpaEntity.class);
        verify(processedSagaEventRepository).save(captor.capture());

        ProcessedSagaEventJpaEntity saved = captor.getValue();
        assertThat(saved.getEventId()).isEqualTo(event.getEventId());
        assertThat(saved.getEventType()).isEqualTo(event.getEventType());
    }
}
