# RabbitMQ 중복 메시지 제로화기 — Redis 기반 3단계 멱등성 설계

> MSA 환경에서 RabbitMQ AT-LEAST-ONCE 정책으로 발생하는 중복 메시지 문제를 Redis + DB 3단계 파이프라인으로 해결한 과정을 공유합니다.
> DLQ 적재 0건, 중복 처리 0회, 중복 차단의 99%를 ~1ms로 처리한 구현입니다.

---

## 1. 문제 발견

### RabbitMQ AT-LEAST-ONCE의 현실

MSA 환경에서 서비스 간 통신에 RabbitMQ를 사용할 때, 가장 먼저 마주치는 현실이 있습니다.

> **"메시지는 반드시 한 번 이상 전달된다."**

RabbitMQ의 기본 정책은 AT-LEAST-ONCE입니다. 네트워크 장애, 소비자 재시작, ACK 누락 등 다양한 이유로 **동일한 메시지가 두 번 이상 수신**될 수 있습니다.

### 실제 발생한 문제

주문 생성 플로우는 다음 Saga로 구성됩니다.

```
주문 서비스                    재고 서비스
────────                    ────────────
주문 생성 (CREATING 상태)
    │
    └─── OrderCreationStartedEvent 발행 (RabbitMQ) ──→ 재고 감소 처리
                                                           │
    ←── ProductStockDecreasedEvent 수신 ───────────────────┘
    │
confirmCreation() → CREATED 상태로 전환
```

여기서 `ProductStockDecreasedEvent`가 **중복 수신**되면:

```
1회차 수신: confirmCreation() → CREATING → CREATED ✅
2회차 수신: confirmCreation() → 이미 CREATED 상태
                              → InvalidOrderStateException ❌
                              → @Retryable 3회 재시도
                              → DLQ 적재
                              → 운영 알람 발생
```

불필요한 예외, 재시도 3회, DLQ 적재, 알람까지 연쇄적으로 발생합니다.
이 흐름이 트래픽 상황에서 반복되면 **DB 커넥션 낭비 + CPU 부하 + 운영 노이즈**가 누적됩니다.

---

## 2. 원인 분석

### 왜 중복이 발생하는가

RabbitMQ에서 소비자가 메시지를 처리한 뒤 ACK를 보내기 전에 연결이 끊기면, 브로커는 메시지를 재전송합니다. 이는 프로토콜 레벨의 동작이라 **애플리케이션에서 직접 제어할 수 없습니다.**

```
소비자: 메시지 수신 → 처리 시작
        처리 완료 → ACK 전송 준비
        [네트워크 순단]
        ACK 미도달 → RabbitMQ가 메시지 재전송
        소비자: 동일 메시지 재수신 → 중복 처리
```

### 기존 코드의 문제

중복 차단 로직 없이 모든 수신 메시지를 바로 처리했습니다.

```java
// Before: 멱등성 처리 없음
@RabbitListener(queues = "order.stock.decreased")
public void receive(ProductStockDecreasedEvent event) {
    orderCommandService.confirmCreation(event.getOrderId(), event.getProductName());
    // 동일 eventId가 두 번 오면 두 번 실행 → 예외 발생
}
```

해결책은 **"이미 처리한 이벤트인지 확인하는 멱등성(Idempotency) 레이어"** 추가입니다.

---

## 3. 해결 전략 — 3단계 멱등성 파이프라인

### 설계 목표

1. **속도**: 대부분의 중복은 빠르게(~1ms) 차단
2. **신뢰성**: Redis 재시작/장애 시에도 중복 처리 방지
3. **일관성**: DB 롤백 시 Redis도 저장 안 됨 → 재처리 가능

### 3단계 파이프라인

```
메시지 수신
    │
    ▼
[1단계] Redis 체크 (~1ms)
    │ HIT  → 즉시 차단, 종료 ───────────────────────── 99%+ 케이스
    │ MISS ↓
    ▼
[2단계] DB 체크 (Redis 재시작 복구용)
    │ EXISTS → Redis 재워밍 후 차단 ─────────────────── Redis 재시작 직후 케이스
    │ NOT EXISTS ↓
    ▼
[3단계] 신규 이벤트 처리
    │
    ├─ 비즈니스 로직 실행 (doProcess)
    ├─ DB에 처리 이력 저장 (processed_saga_events)
    └─ 트랜잭션 커밋 후 Redis 저장 (AFTER_COMMIT)
         └─ DB 롤백 시 Redis 미저장 → 다음 수신 시 재처리 가능 ✅
```

---

## 4. 핵심 구현

### 4-1. 추상화 레이어 — 멱등성과 비즈니스 로직 분리

세 단계 상속 구조로 관심사를 완전히 분리했습니다.

```
AbstractSagaHandler      ← 공통 로깅 (수신/처리완료/실패)
        │
IdempotentSagaHandler    ← 멱등성 3단계 체크
        │
StockDecreasedHandler    ← 비즈니스 로직만 구현
```

**AbstractSagaHandler (공통 로깅)**

```java
// common 모듈 — 모든 Saga 핸들러의 기반
@Slf4j
public abstract class AbstractSagaHandler<T extends SagaEvent> implements SagaEventHandler<T> {

    @Override
    public final void handle(T event) {
        log.info("[SagaEvent] Received | type={} | eventId={} | occurredAt={}",
                event.getEventType(), event.getEventId(), event.getOccurredAt());
        try {
            process(event);
            log.info("[SagaEvent] Processed | type={} | eventId={}",
                    event.getEventType(), event.getEventId());
        } catch (Exception e) {
            log.error("[SagaEvent] Failed | type={} | eventId={} | error={}",
                    event.getEventType(), event.getEventId(), e.getMessage(), e);
            throw e;
        }
    }

    protected abstract void process(T event); // 하위 클래스 구현
}
```

**IdempotentSagaHandler (3단계 멱등성)**

```java
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

        // [1단계] Redis 체크 — 대부분의 중복은 여기서 ~1ms로 차단
        if (sagaExecutor.hasProcessed(redisKey)) {
            log.warn("[Idempotent] Duplicate event skipped | eventId={} | eventType={}",
                    event.getEventId(), event.getEventType());
            return;
        }

        // [2단계] DB 체크 — Redis 재시작 후 캐시 미스 시 복구용
        if (processedSagaEventRepository.existsById(event.getEventId())) {
            log.warn("[Idempotent] Duplicate event skipped | eventId={}",
                    event.getEventId());
            sagaExecutor.rewarmCache(redisKey); // Redis 재워밍 (TTL 재설정)
            return;
        }

        // [3단계] 신규 이벤트: 별도 컴포넌트에서 트랜잭션 + Redis 동기화
        sagaExecutor.executeWithIdempotency(event, redisKey, this::doProcess);
    }

    protected abstract void doProcess(T event); // 비즈니스 로직
}
```

**StockDecreasedHandler (비즈니스 로직만)**

```java
@Component
@RequiredArgsConstructor
public class StockDecreasedHandler extends IdempotentSagaHandler<ProductStockDecreasedEvent> {

    private final OrderCommandService orderCommandService;

    @RabbitListener(queues = OrderRabbitConfig.QUEUE_ORDER_STOCK_DECREASED)
    public void receive(ProductStockDecreasedEvent event) {
        handle(event); // AbstractSagaHandler → IdempotentSagaHandler → doProcess 순서 실행
    }

    @Override
    protected void doProcess(ProductStockDecreasedEvent event) {
        // 멱등성이 보장된 이 시점에서는 순수 비즈니스 로직만
        orderCommandService.confirmCreation(event.getOrderId(), event.getProductName());
    }
}
```

새 이벤트 핸들러를 추가할 때 `doProcess()`만 구현하면 됩니다.
멱등성 로직은 자동으로 적용됩니다.

### 4-2. IdempotentSagaExecutor — DB-Redis 일관성

핵심 설계 포인트는 **Redis를 트랜잭션 커밋 이후에 저장**하는 것입니다.

```java
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
    public <T extends SagaEvent> void executeWithIdempotency(
            T event, String redisKey, Consumer<T> doProcess) {

        // 1. 비즈니스 로직 실행
        doProcess.accept(event);

        // 2. DB에 처리 이력 저장 (트랜잭션 내)
        processedSagaEventRepository.save(
            ProcessedSagaEventJpaEntity.of(event.getEventId(), event.getEventType())
        );

        // 3. 트랜잭션 커밋 성공 후에만 Redis 저장
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                redisTemplate.opsForValue().set(redisKey, "1", REDIS_TTL);
            }
        });
    }
}
```

**왜 AFTER_COMMIT인가?**

```
만약 Redis를 먼저 저장하면:
  Redis 저장 ✅
  비즈니스 로직 실행
  DB 저장 실패 → 트랜잭션 롤백
  → DB는 롤백됐지만 Redis에는 "처리됨"으로 남음
  → 다음 재시도 때 Redis HIT → 차단 → 영원히 처리 안 됨 ❌

AFTER_COMMIT으로 Redis 저장하면:
  비즈니스 로직 실행
  DB 저장
  트랜잭션 커밋 ✅ → 그 후 Redis 저장
  → DB 롤백 시 Redis 미저장 → 다음 재시도 때 정상 처리 ✅
```

### 4-3. ProcessedSagaEventJpaEntity — 영속성 레이어

```java
@Entity
@Table(name = "processed_saga_events", schema = "orders")
public class ProcessedSagaEventJpaEntity {

    @Id
    @Column(name = "event_id", length = 36, updatable = false)
    private String eventId;   // UUID 문자열 → PK 중복 삽입 시 즉시 실패

    @Column(name = "event_type", length = 100, nullable = false, updatable = false)
    private String eventType;

    @Column(name = "processed_at", nullable = false, updatable = false)
    private LocalDateTime processedAt;

    public static ProcessedSagaEventJpaEntity of(String eventId, String eventType) {
        ProcessedSagaEventJpaEntity entity = new ProcessedSagaEventJpaEntity();
        entity.eventId = eventId;
        entity.eventType = eventType;
        entity.processedAt = LocalDateTime.now();
        return entity;
    }
}
```

`event_id`가 PK이므로 동시에 두 요청이 3단계까지 도달해도 **DB 레벨에서 중복 삽입이 막힙니다.**
이것이 레이스 컨디션 방어의 최후 보루입니다.

---

## 5. 별도 컴포넌트 분리의 이유

`IdempotentSagaExecutor`를 별도 `@Component`로 분리한 이유가 있습니다.

```java
// 만약 같은 클래스 내에서 @Transactional 메서드를 호출하면?
public abstract class IdempotentSagaHandler<T> {

    @Transactional  // ← 동작 안 함
    private void executeWithIdempotency(...) { }

    protected final void process(T event) {
        executeWithIdempotency(...); // self-invocation: 프록시 우회 → @Transactional 무시
    }
}
```

Spring의 `@Transactional`은 **프록시 기반**입니다.
같은 클래스 내에서 메서드를 직접 호출하면 프록시를 통하지 않아 트랜잭션이 적용되지 않습니다.

`IdempotentSagaExecutor`를 별도 빈으로 분리해 주입받으면 프록시를 통해 호출되므로 `@Transactional`이 정상 작동합니다.

---

## 6. 성과 측정

K6로 VU 50명이 동일 `eventId`로 100회 중복 메시지를 발행하는 시나리오입니다.

```javascript
// benchmark/k6/03-idempotent-saga.js
// 50 VU가 동시에 동일 eventId로 발행 → 중복 처리 시나리오
export let options = {
  vus: 50,
  iterations: 100,  // 총 100회 발행 (VU당 2회)
};

export default function () {
  // 동일 eventId 고정 → 99건은 중복이어야 함
  const eventPayload = JSON.stringify({
    eventId:   EVENT_ID,  // 고정 UUID
    eventType: 'product.stock.decreased',
    orderId:   ORDER_ID,
    // ...
  });
  // RabbitMQ Management API로 직접 발행
}
```

**검증 쿼리**

```sql
-- 실제 처리 건수 (1이어야 함)
SELECT COUNT(*) FROM orders.processed_saga_events
WHERE event_id = '<EVENT_ID>';

-- DLQ 메시지 수 (0이어야 함)
curl -s -u guest:guest http://localhost:15672/api/queues/%2F/order.product.stock.decreased.dlq \
  | python3 -c "import sys,json; q=json.load(sys.stdin); print('DLQ:', q['messages'])"
```

| 지표 | Before (멱등성 없음) | After (3단계 멱등성) |
|------|---------------------|---------------------|
| 실제 처리 건수 | 100건 | **1건** |
| InvalidOrderStateException | 99회 발생 | **0회** |
| DLQ 적재 | 99건 | **0건** |
| 중복 차단 속도 | - | **~1ms (Redis)** |
| 운영 알람 발생 | 다수 | **0건** |

---

## 7. 트레이드오프와 배운 점

### Redis TTL 24시간의 의미

현재 TTL은 24시간입니다.

```java
private static final Duration REDIS_TTL = Duration.ofHours(24);
```

24시간이 지난 이벤트가 재수신되면 Redis에서는 신규로 판단하고, DB로 2단계 체크합니다.
DB에는 영구 저장되어 있으므로 최종적으로 차단됩니다.

TTL이 너무 짧으면 Redis 의미가 없고, 너무 길면 메모리 낭비입니다.
**"RabbitMQ가 동일 메시지를 얼마나 오랫동안 재전송할 수 있는가"** 에 맞춰 설정합니다.

### 레이스 컨디션 가능성

3단계 체크 사이에 매우 짧은 시간에 두 요청이 동시에 통과할 수 있습니다.

```
요청 A: Redis MISS → DB MISS → executeWithIdempotency 진입
요청 B: Redis MISS → DB MISS → executeWithIdempotency 진입 (거의 동시)
→ 둘 다 3단계 도달
→ DB PK 중복 → 한 쪽 실패 → @Retryable → 재시도 시 Redis HIT → 차단
```

현실에서 이 케이스는 극히 드물고, DB PK 제약이 최후 방어선이 되어 결과적으로 1건만 처리됩니다.

### 핵심 배움

> **"멱등성은 선택이 아니라 MSA에서의 기본 설계 원칙이다."**

AT-LEAST-ONCE가 보장되는 메시지 브로커를 사용하는 순간, 컨슈머는 반드시 멱등해야 합니다.
추상화 레이어 덕분에 새 이벤트 핸들러를 추가할 때 멱등성 고민 없이 `doProcess()`만 구현하면 됩니다.

---

## 마치며

| 주제 | 핵심 |
|------|------|
| 3단계 파이프라인 | Redis(속도) → DB(신뢰성) → 처리(비즈니스) |
| AFTER_COMMIT | DB 커밋 성공 시에만 Redis 저장 → DB-Redis 일관성 |
| 추상화 계층 | 멱등성/로깅/비즈니스 로직 완전 분리 |
| self-invocation 우회 | 별도 컴포넌트 분리로 @Transactional 프록시 보장 |

RabbitMQ를 사용하면서 "설마 중복이 오겠어"라고 생각했다가는 운영에서 반드시 마주치게 됩니다.
멱등성은 처음부터 설계에 포함시키는 것이 훨씬 저렴합니다.

---

**GitHub:** [https://github.com/kim-jun-won/ship-flow](https://github.com/kim-jun-won/ship-flow)
**테스트 환경:** K6 v0.49+, RabbitMQ 3.x, Redis 7.x, Spring Boot 3.x, Java 21
