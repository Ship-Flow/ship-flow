package com.shipflow.orderservice.infrastructure.messaging.handler;

import com.shipflow.common.messaging.event.SagaEvent;
import com.shipflow.orderservice.infrastructure.persistence.ProcessedSagaEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdempotentSagaHandlerTest {

    // ── 테스트용 이벤트 ──────────────────────────────────────────────────────────
    static class TestEvent extends SagaEvent {
        TestEvent() {
            super("test.event");
        }
    }

    // ── 테스트용 핸들러 구현체 ────────────────────────────────────────────────────
    static class TestHandler extends IdempotentSagaHandler<TestEvent> {
        boolean doProcessCalled = false;

        @Override
        protected void doProcess(TestEvent event) {
            doProcessCalled = true;
        }
    }

    // ── Mocks ────────────────────────────────────────────────────────────────────
    @Mock
    private ProcessedSagaEventRepository processedSagaEventRepository;

    @Mock
    private IdempotentSagaExecutor sagaExecutor;

    private TestHandler handler;
    private TestEvent event;

    @BeforeEach
    void setUp() {
        handler = new TestHandler();
        // @Autowired 필드이므로 ReflectionTestUtils 로 주입
        ReflectionTestUtils.setField(handler, "processedSagaEventRepository", processedSagaEventRepository);
        ReflectionTestUtils.setField(handler, "sagaExecutor", sagaExecutor);

        event = new TestEvent();
    }

    // ── 1. Redis HIT → doProcess 미호출 ──────────────────────────────────────────
    @Test
    void Redis_캐시_HIT시_중복이벤트_스킵() {
        String expectedKey = "idempotent:saga:" + event.getEventId();
        when(sagaExecutor.hasProcessed(expectedKey)).thenReturn(true);

        handler.handle(event);

        verify(sagaExecutor).hasProcessed(expectedKey);
        verify(sagaExecutor, never()).executeWithIdempotency(any(), any(), any());
        verify(processedSagaEventRepository, never()).existsById(any());
        assertThat(handler.doProcessCalled).isFalse();
    }

    // ── 2. Redis MISS + DB HIT → doProcess 미호출 + rewarmCache 호출 ─────────────
    @Test
    void Redis_캐시_MISS_DB_HIT시_중복이벤트_스킵_후_캐시_재워밍() {
        String expectedKey = "idempotent:saga:" + event.getEventId();
        when(sagaExecutor.hasProcessed(expectedKey)).thenReturn(false);
        when(processedSagaEventRepository.existsById(event.getEventId())).thenReturn(true);

        handler.handle(event);

        verify(sagaExecutor).hasProcessed(expectedKey);
        verify(processedSagaEventRepository).existsById(event.getEventId());
        verify(sagaExecutor).rewarmCache(expectedKey);
        verify(sagaExecutor, never()).executeWithIdempotency(any(), any(), any());
        assertThat(handler.doProcessCalled).isFalse();
    }

    // ── 3. Redis MISS + DB MISS → executeWithIdempotency 호출 ────────────────────
    @Test
    void Redis_캐시_MISS_DB_MISS시_신규이벤트_처리() {
        String expectedKey = "idempotent:saga:" + event.getEventId();
        when(sagaExecutor.hasProcessed(expectedKey)).thenReturn(false);
        when(processedSagaEventRepository.existsById(event.getEventId())).thenReturn(false);

        handler.handle(event);

        verify(sagaExecutor).hasProcessed(expectedKey);
        verify(processedSagaEventRepository).existsById(event.getEventId());
        verify(sagaExecutor).executeWithIdempotency(eq(event), eq(expectedKey), any());
        verify(sagaExecutor, never()).rewarmCache(any());
    }
}
