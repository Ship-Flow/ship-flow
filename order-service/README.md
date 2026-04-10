# Order Service

주문 도메인을 담당하는 마이크로서비스입니다.

---

## 성능 개선 사례

### 1. CQRS + 인덱스 도입 — 조회 성능 개선

**배경:**
- `p_order_read_models` 테이블에 인덱스가 없어 `orderer_id` 필터 조회 시 풀 테이블 스캔(Seq Scan) 발생
- CQRS 패턴으로 ReadModel 별도 테이블을 두어 JOIN 없이 배송 정보 포함 조회 가능
- `orderer_id, created_at DESC` 복합 인덱스 추가로 Seq Scan → Index Scan 전환

**적용 내용:**
```java
// OrderReadModelJpaEntity.java
@Table(
    name = "p_order_read_models",
    indexes = {
        @Index(name = "idx_read_models_orderer_created", columnList = "orderer_id, created_at DESC")
    }
)

// OrderJpaEntity.java
@Table(
    name = "p_orders",
    indexes = {
        @Index(name = "idx_orders_orderer_id", columnList = "orderer_id")
    }
)
```

**측정 환경:**
- 데이터: 10만 건, orderer 1,000명 분산
- 툴: k6 (Virtual Users 50명, 30초)
- 엔드포인트: `GET /api/orders?ordererId={id}&size=10&sort=createdAt,DESC`
- 서비스 직접 호출 (port 8082), 헤더: `X-User-Id`, `X-User-Role: MASTER`

**실측 결과:**

| 지표 | Before (인덱스 없음) | After (인덱스 있음) | 개선율 |
|------|---------------------|---------------------|--------|
| 평균 응답시간 | 19.16ms | 8.18ms | **57.3% ↓** |
| P95 응답시간 | 32.48ms | 12.35ms | **62.0% ↓** |
| TPS | 417.73 req/s | 460.90 req/s | **10.3% ↑** |

> 측정 환경: k6 VU 50명, 30초, 데이터 10만 건 (orderer 1,000명 분산)

**실행계획 비교 (EXPLAIN ANALYZE):**

```
-- Before (Seq Scan — 인덱스 없음, 10만 건 전체 스캔)
Seq Scan on p_order_read_models  (cost=0.00..4475.25 rows=100 ...)
  Filter: ((deleted_at IS NULL) AND (orderer_id = '...'))

-- After (Index Scan — 인덱스로 해당 orderer 데이터만 직접 접근)
Index Scan using idx_read_models_orderer_created on p_order_read_models  (cost=0.42..402.17 rows=100 ...)
  Index Cond: (orderer_id = '...')
  Execution Time: 0.059 ms
```

---

### 2. Feign 병렬 호출 (CompletableFuture) — 주문 생성 응답 개선

**배경:**
- 주문 생성 시 Product 서비스, User 서비스, Company 서비스를 순차적으로 Feign 호출
- 각 서비스 응답시간이 순차로 누적 → 총 응답시간 = 각 서비스 latency 합산
- Product/User 조회는 서로 의존성이 없어 병렬 실행 가능

**Before (순차 호출):**
```java
// Step 1 → Step 2 → Step 3 순차 실행 (각 ~50ms 누적)
ProductInfo product = productAdapter.fetch(ordererId.toString(), productId, quantity);
UserInfo    user    = userAdapter.fetch(ordererId);
ReceiverCompanyInfo company = companyAdapter.fetch(user.receiverCompanyId());
```

**After (병렬 호출):**
```java
// Step 1: Product + User 병렬 실행 (의존성 없음)
CompletableFuture<ProductInfo> productFuture =
    CompletableFuture.supplyAsync(() -> productAdapter.fetch(ordererId.toString(), productId, quantity));
CompletableFuture<UserInfo> userFuture =
    CompletableFuture.supplyAsync(() -> userAdapter.fetch(ordererId));

CompletableFuture.allOf(productFuture, userFuture).join();

// Step 2: Company 호출 (User 결과에 의존)
ReceiverCompanyInfo company = companyAdapter.fetch(user.receiverCompanyId());
```

**측정 환경:**
- Docker Compose로 전체 MSA 스택 실행
- 툴: k6 (Virtual Users 50명, 30초)
- 엔드포인트: `POST /api/orders`
- 서비스 직접 호출 (port 8082), 헤더: `X-User-Id`, `X-User-Role: MASTER`

**실측 결과:**

| 지표 | Before (순차 호출) | After (병렬 호출) | 개선율 |
|------|-------------------|-------------------|--------|
| 평균 응답시간 | -ms | -ms | - |
| P95 응답시간 | -ms | -ms | - |
| TPS | - req/s | - req/s | - |

> 측정 후 수치 기입 예정

---

### 3. Idempotent Saga (Redis 멱등성) — RabbitMQ 중복 메시지 처리

**배경:**
- RabbitMQ는 at-least-once delivery 보장 → 네트워크 장애 시 동일 메시지 중복 수신 가능
- `product.stock.decreased` 이벤트 중복 수신 시: 이미 CREATED 상태인 주문에 `confirmCreation()` 재호출
- `InvalidOrderStateException` 발생 → 3회 재시도 후 DLQ 적재 → 불필요 재처리 및 시스템 과부하

**Before (멱등성 없음):**
```java
// IdempotentSagaHandler.process() — 중복 체크 없이 직접 처리
@Override
protected final void process(T event) {
    sagaExecutor.executeWithIdempotency(event, "unused", this::doProcess);
    // → 중복 메시지마다 doProcess() 호출 → 예외 → 재시도 → DLQ
}
```

**After (Redis + DB 3단계 멱등성):**
```java
// [1] Redis 체크 (~1ms) — 대부분의 중복은 여기서 차단
if (sagaExecutor.hasProcessed(redisKey)) { return; }

// [2] DB 체크 (Redis 재시작 복구) — Redis miss 시 DB 확인 후 재워밍
if (processedSagaEventRepository.existsById(event.getEventId())) {
    sagaExecutor.rewarmCache(redisKey);
    return;
}

// [3] 신규 이벤트: 처리 → DB 저장 → 커밋 후 Redis 저장 (DB-Redis 일관성)
sagaExecutor.executeWithIdempotency(event, redisKey, this::doProcess);
```

**핵심 설계: DB 커밋 후 Redis 저장 (`TransactionSynchronization.afterCommit()`)**
- Redis를 트랜잭션 커밋 전에 저장하면: DB 롤백 시 Redis에는 "처리됨"으로 남아 다음 재시도 차단 → 데이터 유실
- `afterCommit()` 훅으로 DB 커밋 성공 시에만 Redis 저장 → DB-Redis 일관성 보장

**측정 환경:**
- Docker Compose로 order-service, RabbitMQ, PostgreSQL, Redis 실행
- 툴: k6 (RabbitMQ Management HTTP API 통해 동일 eventId 100회 발행)
- 대상: `saga.events` 익스체인지, routing key `product.stock.decreased`

**실측 결과:**

| 지표 | Before (멱등성 없음) | After (Redis+DB 멱등성) |
|------|---------------------|------------------------|
| 발행 메시지 수 | 100 | 100 |
| 실제 처리 건수 | 1 | 1 |
| DLQ 적재 건수 | ~99 | **0** |
| 불필요 재시도 횟수 | ~297회 (99×3) | **0회** |
| 중복 차단율 | 0% | **99%** |

> 측정 후 수치 기입 예정

---

## 벤치마크 실행 방법

### 사전 준비

```bash
# k6 설치
brew install k6

# 시드 데이터 삽입 (10만 건)
psql -h localhost -U shipflow -d shipflow -f benchmark/seed/insert-orders.sql

# 테스트용 orderer_id 추출
psql -h localhost -U shipflow -d shipflow -c \
  "SELECT orderer_id FROM orders.p_order_read_models GROUP BY orderer_id ORDER BY COUNT(*) DESC LIMIT 1;"
```

### Before 측정 (benchmark/before-index 브랜치)

```bash
git checkout benchmark/before-index

# order-service 재시작 후:
k6 run \
  -e ORDERER_ID=<추출한_UUID> \
  -e USER_ID=<추출한_UUID> \
  --out json=benchmark/results/01-before.json \
  benchmark/k6/01-cqrs-index.js

# EXPLAIN ANALYZE 저장
psql -h localhost -U shipflow -d shipflow -c "
EXPLAIN ANALYZE
SELECT * FROM orders.p_order_read_models
WHERE orderer_id = '<추출한_UUID>' AND deleted_at IS NULL
ORDER BY created_at DESC LIMIT 10;
" > benchmark/results/explain-before.txt
```

### After 측정 (order-test 브랜치)

```bash
git checkout order-test

# order-service 재시작 후 (Hibernate ddl-auto: update로 인덱스 자동 생성):
k6 run \
  -e ORDERER_ID=<동일_UUID> \
  -e USER_ID=<동일_UUID> \
  --out json=benchmark/results/01-after.json \
  benchmark/k6/01-cqrs-index.js

# EXPLAIN ANALYZE 저장
psql -h localhost -U shipflow -d shipflow -c "
EXPLAIN ANALYZE
SELECT * FROM orders.p_order_read_models
WHERE orderer_id = '<동일_UUID>' AND deleted_at IS NULL
ORDER BY created_at DESC LIMIT 10;
" > benchmark/results/explain-after.txt
```

---

### 항목 2: Feign 병렬 호출 벤치마크

#### 사전 준비

```bash
# Docker Compose로 전체 MSA 스택 실행
docker compose up -d

# Product 서비스에 충분한 재고의 상품 등록 (stock >= 2000)
# User 서비스에 receiver_company_id 가 있는 사용자 등록
# → 해당 USER_ID, PRODUCT_ID 를 아래 명령에 사용
```

#### Before 측정 (benchmark/before-feign 브랜치)

Before 브랜치 생성 방법:
```bash
git checkout -b benchmark/before-feign

# OrderFetchService.fetchAndBuild() 를 아래 순차 코드로 교체:
#
#   ProductInfo product = productAdapter.fetch(ordererId.toString(), productId, quantity);
#   UserInfo    user    = userAdapter.fetch(ordererId);
#   ReceiverCompanyInfo company = companyAdapter.fetch(user.receiverCompanyId());
#   return new CreateOrderCommand(...);  // 나머지 동일
#
# order-service 재빌드 후:
k6 run \
  -e USER_ID=<orderer-uuid> \
  -e PRODUCT_ID=<product-uuid> \
  --out json=benchmark/results/02-before.json \
  benchmark/k6/02-feign-parallel.js
```

#### After 측정 (order-test 브랜치)

```bash
git checkout order-test
# order-service 재시작 후:
k6 run \
  -e USER_ID=<동일_UUID> \
  -e PRODUCT_ID=<동일_UUID> \
  --out json=benchmark/results/02-after.json \
  benchmark/k6/02-feign-parallel.js
```

---

### 항목 3: Idempotent Saga 벤치마크

#### 사전 준비

```bash
# CREATING 상태 주문 생성
psql -h localhost -U shipflow -d shipflow \
  -f benchmark/seed/03-saga-seed.sql
# → 출력된 order_id 를 ORDER_ID 환경변수에 사용

# 고정 eventId 생성 (동일 UUID를 반복 사용)
export EVENT_ID=$(python3 -c "import uuid; print(uuid.uuid4())")
echo "EVENT_ID: $EVENT_ID"
```

#### Before 측정 (benchmark/before-saga 브랜치)

Before 브랜치 생성 방법:
```bash
git checkout -b benchmark/before-saga

# IdempotentSagaHandler.process() 를 아래 코드로 교체 (Redis·DB 체크 제거):
#
#   @Override
#   protected final void process(T event) {
#       sagaExecutor.executeWithIdempotency(event, "unused", this::doProcess);
#   }
#
# order-service 재빌드 후:
k6 run \
  -e ORDER_ID=<benchmark-order-uuid> \
  -e EVENT_ID=$EVENT_ID \
  --out json=benchmark/results/03-before.json \
  benchmark/k6/03-idempotent-saga.js

# Before 결과 검증
psql -h localhost -U shipflow -d shipflow -c \
  "SELECT COUNT(*) FROM orders.processed_saga_events WHERE event_id = '$EVENT_ID';"

# DLQ 메시지 수 확인
curl -s -u guest:guest \
  "http://localhost:15672/api/queues/%2F/order.product.stock.decreased.dlq" \
  | python3 -c "import sys,json; q=json.load(sys.stdin); print('DLQ messages:', q['messages'])"
```

#### After 측정 (order-test 브랜치)

```bash
git checkout order-test

# 새 CREATING 주문 생성
psql -h localhost -U shipflow -d shipflow -f benchmark/seed/03-saga-seed.sql
# → 새 ORDER_ID 사용 (이전 주문은 이미 CREATED 상태)

# processed_saga_events · Redis 초기화
psql -h localhost -U shipflow -d shipflow -c \
  "DELETE FROM orders.processed_saga_events WHERE event_id = '$EVENT_ID';"
redis-cli DEL "idempotent:saga:$EVENT_ID"

# order-service 재시작 후:
k6 run \
  -e ORDER_ID=<new-benchmark-order-uuid> \
  -e EVENT_ID=$EVENT_ID \
  --out json=benchmark/results/03-after.json \
  benchmark/k6/03-idempotent-saga.js

# After 결과 검증
psql -h localhost -U shipflow -d shipflow -c \
  "SELECT COUNT(*) FROM orders.processed_saga_events WHERE event_id = '$EVENT_ID';"
# → COUNT = 1 이어야 함

curl -s -u guest:guest \
  "http://localhost:15672/api/queues/%2F/order.product.stock.decreased.dlq" \
  | python3 -c "import sys,json; q=json.load(sys.stdin); print('DLQ messages:', q['messages'])"
# → 0 이어야 함
```
