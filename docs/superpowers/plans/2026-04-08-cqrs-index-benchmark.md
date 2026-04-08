# CQRS + 인덱스 조회 성능 벤치마크 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** CQRS ReadModel과 DB 인덱스 도입 전/후의 조회 성능을 k6로 실측하고, README에 수치로 기록한다.

**Architecture:** `benchmark/before-index` 브랜치(인덱스 없는 현재 상태)에서 k6로 Before 수치를 측정하고, `order-test` 브랜치에서 JPA `@Table(indexes=...)` 어노테이션으로 인덱스를 추가한 뒤 After 수치를 측정한다. API 인증은 order-service(포트 8082)에 직접 `X-User-Id`, `X-User-Role` 헤더를 전달하는 방식으로 게이트웨이를 우회한다.

**Tech Stack:** k6, PostgreSQL, Spring Data JPA (`@Table` indexes), SQL generate_series

---

## 파일 구조

| 파일 | 역할 |
|------|------|
| `benchmark/seed/insert-orders.sql` | 10만 건 주문+ReadModel 데이터 삽입 |
| `benchmark/k6/01-cqrs-index.js` | Before/After 동일 시나리오 k6 스크립트 |
| `order-service/src/main/java/.../OrderReadModelJpaEntity.java` | 인덱스 어노테이션 추가 |
| `order-service/src/main/java/.../OrderJpaEntity.java` | 인덱스 어노테이션 추가 |
| `order-service/README.md` | Before/After 수치 표 + EXPLAIN ANALYZE 이미지 |

---

## Task 1: benchmark/before-index 브랜치 생성

**Files:**
- (브랜치 생성만, 파일 변경 없음)

- [ ] **Step 1: 현재 브랜치 확인**

```bash
git branch
```
Expected: `* order-test` 표시

- [ ] **Step 2: Before 브랜치 생성**

```bash
git checkout -b benchmark/before-index
```
Expected: `Switched to a new branch 'benchmark/before-index'`

> 이 브랜치는 인덱스가 없는 현재 상태를 보존한다. 이후 변경 없이 유지.

---

## Task 2: 시드 데이터 스크립트 작성

**Files:**
- Create: `benchmark/seed/insert-orders.sql`

- [ ] **Step 1: benchmark/seed 디렉토리 생성 후 SQL 작성**

파일 생성: `benchmark/seed/insert-orders.sql`

```sql
-- ============================================================
-- 10만 건 주문 + ReadModel 시드 데이터 삽입
-- orderer_id는 1,000명 분산 (UUID 기반)
-- ============================================================

DO $$
DECLARE
    orderer_ids UUID[] := ARRAY(
        SELECT gen_random_uuid() FROM generate_series(1, 1000)
    );
    orderer_id UUID;
    order_id   UUID;
    i          INT;
BEGIN
    FOR i IN 1..100000 LOOP
        orderer_id := orderer_ids[1 + (i % 1000)];
        order_id   := gen_random_uuid();

        INSERT INTO orders.p_orders (
            id, orderer_id, product_id,
            supplier_company_id, receiver_company_id,
            departure_hub_id, arrival_hub_id,
            value, status,
            created_at, updated_at
        ) VALUES (
            order_id,
            orderer_id,
            gen_random_uuid(),
            gen_random_uuid(),
            gen_random_uuid(),
            gen_random_uuid(),
            gen_random_uuid(),
            (random() * 1000)::INT + 1,
            (ARRAY['CREATED','COMPLETED','CANCELED'])[1 + (i % 3)],
            NOW() - (random() * INTERVAL '365 days'),
            NOW() - (random() * INTERVAL '30 days')
        );

        INSERT INTO orders.p_order_read_models (
            order_id, orderer_id, orderer_name,
            product_id, product_name,
            order_status,
            supplier_company_id, supplier_company_name,
            receiver_company_id, receiver_company_name,
            departure_hub_id, departure_hub_name,
            arrival_hub_id,   arrival_hub_name,
            quantity,
            created_at, updated_at
        ) VALUES (
            order_id,
            orderer_id,
            'user_' || (i % 1000),
            gen_random_uuid(),
            'product_' || (i % 50),
            (ARRAY['CREATED','COMPLETED','CANCELED'])[1 + (i % 3)],
            gen_random_uuid(), 'supplier_' || (i % 20),
            gen_random_uuid(), 'receiver_' || (i % 20),
            gen_random_uuid(), 'hub_' || (i % 10),
            gen_random_uuid(), 'hub_' || (i % 10),
            (random() * 100)::INT + 1,
            NOW() - (random() * INTERVAL '365 days'),
            NOW() - (random() * INTERVAL '30 days')
        );
    END LOOP;
END $$;
```

---

## Task 3: k6 스크립트 작성

**Files:**
- Create: `benchmark/k6/01-cqrs-index.js`

- [ ] **Step 1: benchmark/k6 디렉토리 생성 후 스크립트 작성**

파일 생성: `benchmark/k6/01-cqrs-index.js`

```javascript
import http from 'k6/http';
import { check, sleep } from 'k6';

// k6 run -e ORDERER_ID=<uuid> benchmark/k6/01-cqrs-index.js
// ORDERER_ID: 시드 데이터의 orderer_ids 중 하나 (p_order_read_models에서 SELECT DISTINCT orderer_id LIMIT 1)

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8082';
const ORDERER_ID = __ENV.ORDERER_ID || 'replace-with-real-uuid';
const USER_ID    = __ENV.USER_ID    || ORDERER_ID;

export let options = {
  vus: 50,
  duration: '30s',
  thresholds: {
    http_req_duration: ['p(95)<3000'], // 임계값 (Before 단계에서는 느릴 수 있음)
  },
};

const HEADERS = {
  'X-User-Id':   USER_ID,
  'X-User-Role': 'MASTER',  // MASTER 권한 → 타인 주문도 조회 가능
};

export default function () {
  const url = `${BASE_URL}/api/orders?ordererId=${ORDERER_ID}&size=10&page=0&sort=createdAt,DESC`;
  const res = http.get(url, { headers: HEADERS });

  check(res, {
    'status is 200': (r) => r.status === 200,
    'has content':   (r) => r.body.length > 0,
  });

  sleep(0.1);
}
```

---

## Task 4: Before 수치 측정

**Files:**
- Create: `benchmark/results/01-before.json`

- [ ] **Step 1: benchmark/before-index 브랜치 확인**

```bash
git branch
```
Expected: `* benchmark/before-index`

- [ ] **Step 2: 서비스 기동 확인**

```bash
curl -s http://localhost:8082/actuator/health | grep '"status":"UP"'
```
Expected: `"status":"UP"` 포함 응답

- [ ] **Step 3: 시드 데이터 삽입**

```bash
psql -h localhost -U shipflow -d shipflow -f benchmark/seed/insert-orders.sql
```
Expected: `DO` 메시지 출력, 오류 없음

- [ ] **Step 4: 시드 데이터 확인**

```bash
psql -h localhost -U shipflow -d shipflow -c "SELECT COUNT(*) FROM orders.p_order_read_models;"
```
Expected: `100000` 이상

- [ ] **Step 5: 테스트용 orderer_id 추출**

```bash
psql -h localhost -U shipflow -d shipflow -c \
  "SELECT orderer_id FROM orders.p_order_read_models GROUP BY orderer_id ORDER BY COUNT(*) DESC LIMIT 1;"
```
Expected: UUID 1개 출력. 이 값을 다음 단계의 ORDERER_ID로 사용.

- [ ] **Step 6: k6 Before 측정 실행**

```bash
mkdir -p benchmark/results
k6 run \
  -e ORDERER_ID=<위에서_추출한_UUID> \
  -e USER_ID=<위에서_추출한_UUID> \
  --out json=benchmark/results/01-before.json \
  benchmark/k6/01-cqrs-index.js
```

- [ ] **Step 7: 주요 수치 기록**

k6 출력에서 다음 수치를 메모:
- `http_req_duration` → avg, p(95)
- `http_reqs` → rate (TPS)

---

## Task 5: EXPLAIN ANALYZE Before 캡처

- [ ] **Step 1: Before 실행계획 확인 및 저장**

```bash
psql -h localhost -U shipflow -d shipflow -c "
EXPLAIN ANALYZE
SELECT *
FROM orders.p_order_read_models
WHERE orderer_id = '<위에서_추출한_UUID>'
  AND deleted_at IS NULL
ORDER BY created_at DESC
LIMIT 10;
" > benchmark/results/explain-before.txt
```

- [ ] **Step 2: 출력 확인**

```bash
cat benchmark/results/explain-before.txt
```
Expected: `Seq Scan on p_order_read_models` 포함 (인덱스 없으면 풀 스캔)

---

## Task 6: order-test 브랜치로 전환 후 인덱스 추가

**Files:**
- Modify: `order-service/src/main/java/com/shipflow/orderservice/infrastructure/persistence/OrderReadModelJpaEntity.java`
- Modify: `order-service/src/main/java/com/shipflow/orderservice/infrastructure/persistence/OrderJpaEntity.java`

- [ ] **Step 1: order-test 브랜치로 전환**

```bash
git checkout order-test
```
Expected: `Switched to branch 'order-test'`

- [ ] **Step 2: OrderReadModelJpaEntity에 인덱스 어노테이션 추가**

파일: `order-service/src/main/java/com/shipflow/orderservice/infrastructure/persistence/OrderReadModelJpaEntity.java`

`@Table(name = "p_order_read_models")` 줄을 아래로 교체:

```java
@Table(
    name = "p_order_read_models",
    indexes = {
        @Index(name = "idx_read_models_orderer_created",
               columnList = "orderer_id, created_at DESC")
    }
)
```

import 추가 (파일 상단):
```java
import jakarta.persistence.Index;
```

- [ ] **Step 3: OrderJpaEntity에 인덱스 어노테이션 추가**

파일: `order-service/src/main/java/com/shipflow/orderservice/infrastructure/persistence/OrderJpaEntity.java`

`@Table(name = "p_orders")` 줄을 아래로 교체:

```java
@Table(
    name = "p_orders",
    indexes = {
        @Index(name = "idx_orders_orderer_id", columnList = "orderer_id")
    }
)
```

import 추가 (파일 상단):
```java
import jakarta.persistence.Index;
```

- [ ] **Step 4: 서비스 재시작 (Hibernate ddl-auto: update가 인덱스 자동 생성)**

order-service 재시작 후 로그에서 인덱스 생성 확인:
```
create index idx_read_models_orderer_created on p_order_read_models (orderer_id, created_at desc)
```

- [ ] **Step 5: 인덱스 생성 DB 확인**

```bash
psql -h localhost -U shipflow -d shipflow -c \
  "\di orders.*idx*"
```
Expected: `idx_read_models_orderer_created`, `idx_orders_orderer_id` 출력

---

## Task 7: After 수치 측정

**Files:**
- Create: `benchmark/results/01-after.json`

- [ ] **Step 1: k6 After 측정 실행**

```bash
k6 run \
  -e ORDERER_ID=<Task_4에서_사용한_동일_UUID> \
  -e USER_ID=<Task_4에서_사용한_동일_UUID> \
  --out json=benchmark/results/01-after.json \
  benchmark/k6/01-cqrs-index.js
```

- [ ] **Step 2: 주요 수치 기록**

k6 출력에서:
- `http_req_duration` → avg, p(95)
- `http_reqs` → rate (TPS)

---

## Task 8: EXPLAIN ANALYZE After 캡처

- [ ] **Step 1: After 실행계획 확인 및 저장**

```bash
psql -h localhost -U shipflow -d shipflow -c "
EXPLAIN ANALYZE
SELECT *
FROM orders.p_order_read_models
WHERE orderer_id = '<동일_UUID>'
  AND deleted_at IS NULL
ORDER BY created_at DESC
LIMIT 10;
" > benchmark/results/explain-after.txt
```

- [ ] **Step 2: 출력 확인**

```bash
cat benchmark/results/explain-after.txt
```
Expected: `Index Scan using idx_read_models_orderer_created` 포함

- [ ] **Step 3: 두 실행계획 비교**

```bash
echo "=== BEFORE ===" && cat benchmark/results/explain-before.txt
echo "=== AFTER ===" && cat benchmark/results/explain-after.txt
```

---

## Task 9: README 작성

**Files:**
- Create: `order-service/README.md`

- [ ] **Step 1: README에 성능 개선 섹션 작성**

실측된 수치를 아래 템플릿에 채워 넣는다:

```markdown
# Order Service

## 성능 개선 사례

### 1. CQRS + 인덱스 도입 — 조회 성능 개선

**배경:**
- `p_order_read_models` 테이블에 인덱스가 없어 orderer_id 필터 조회 시 풀 테이블 스캔 발생
- CQRS 패턴으로 ReadModel 별도 테이블을 두어 JOIN 없이 배송 정보 포함 조회 가능
- 인덱스 추가로 Seq Scan → Index Scan 전환

**측정 환경:**
- 데이터: 10만 건, orderer 1,000명 분산
- 툴: k6 (VU 50, 30초)
- 엔드포인트: `GET /api/orders?ordererId={id}&size=10`

**결과:**

| 지표 | Before (인덱스 없음) | After (인덱스 있음) | 개선율 |
|------|---------------------|---------------------|--------|
| 평균 응답시간 | {before_avg}ms | {after_avg}ms | {rate}% ↓ |
| P95 응답시간  | {before_p95}ms | {after_p95}ms | {rate}% ↓ |
| TPS           | {before_tps}   | {after_tps}   | {rate}% ↑ |

**실행계획 비교:**

Before: `Seq Scan on p_order_read_models (cost=0.00..XXXX rows=XXXX)`
After:  `Index Scan using idx_read_models_orderer_created (cost=0.00..XX rows=XX)`
```

> `{...}` 부분은 Task 4, 7에서 실측한 수치로 교체
