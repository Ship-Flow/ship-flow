# 성능 개선 벤치마크 스펙

**날짜:** 2026-04-08
**도메인:** order-service
**목적:** 주문 도메인의 3가지 성능 최적화를 포트폴리오용으로 실측 수치화

---

## 개요

| 개선 항목 | 구현 상태 | 측정 방법 |
|-----------|-----------|-----------|
| CQRS + DB 인덱스 | 인덱스 미구현 → 추가 예정 | k6 + EXPLAIN ANALYZE |
| Redis 중복 소비 방지 | 구현 완료 | 플래그 On/Off + 메시지 중복 발송 |
| Feign 병렬 호출 | 구현 완료 | k6 + Before 브랜치(순차 호출) |

---

## 측정 환경

- **부하 테스트 툴:** k6
- **데이터 규모:** 10만 건 (orderer_id 약 1,000명 분산)
- **외부 서비스 모킹:** WireMock (Feign 병렬 테스트용, 50ms 지연 스텁)
- **Before/After 구분:** 브랜치 분리 또는 설정 플래그

---

## 디렉토리 구조

```
benchmark/
├── k6/
│   ├── 01-cqrs-index.js          # 인덱스 Before/After 시나리오
│   ├── 02-redis-idempotency.js   # 중복 소비 시나리오
│   └── 03-feign-parallel.js      # 병렬 호출 시나리오
├── seed/
│   └── insert-orders.sql         # 10만 건 데이터 삽입
└── results/                      # k6 --out json 결과 저장
    ├── 01-before.json
    ├── 01-after.json
    └── ...
```

---

## 항목 1: CQRS + 인덱스 조회 성능

### 브랜치 전략
- **Before:** `benchmark/before-index` — 인덱스 없는 현재 상태 (test-schema.sql 그대로)
- **After:** `order-test` — 아래 인덱스 추가

### 추가할 인덱스
```sql
CREATE INDEX idx_orders_orderer_id
  ON orders.p_orders(orderer_id);

CREATE INDEX idx_read_models_orderer_created
  ON orders.p_order_read_models(orderer_id, created_at DESC);
```

### k6 시나리오 (`benchmark/k6/01-cqrs-index.js`)
- VU: 50명
- Duration: 30초
- Endpoint: `GET /api/orders?ordererId={id}`

### EXPLAIN ANALYZE 쿼리
```sql
EXPLAIN ANALYZE
SELECT * FROM orders.p_order_read_models
WHERE orderer_id = '<uuid>'
ORDER BY created_at DESC
LIMIT 10;
```

### 기대 수치
| 지표 | Before (인덱스 없음) | After (인덱스 있음) | 개선율 |
|------|---------------------|---------------------|--------|
| 평균 응답시간 | ~800ms | ~30ms | **96% ↓** |
| P95 응답시간 | ~1500ms | ~60ms | **96% ↓** |
| TPS | ~15 | ~300 | **1900% ↑** |

---

## 항목 2: Redis 중복 소비 방지

### Before/After 구분
브랜치 분리 대신 **설정 플래그**로 Redis 체크 활성화/비활성화

**`application.yaml`에 추가:**
```yaml
saga:
  idempotency:
    enabled: true   # false → Before 상태 재현
```

**수정 파일:** `IdempotentSagaExecutor.java`
플래그가 false일 때 `hasProcessed()` 체크 스킵

### 측정 시나리오
- 동일 `eventId`의 메시지를 100번 연속 발송
- `p_orders` 테이블 상태 변경 횟수 카운트

### 기대 수치
| 지표 | Before (Redis 없음) | After (Redis 있음) |
|------|--------------------|--------------------|
| 중복 처리 횟수 (100건 발송) | 100회 | 1회 |
| 중복 방지율 | 0% | **99%** |
| 단건 처리 시간 | ~5ms | ~6ms |
| 100건 총 처리 시간 | ~500ms | **~6ms** |

---

## 항목 3: Feign 병렬 호출

### 브랜치 전략
- **Before:** `benchmark/before-parallel` — `OrderFetchService`를 순차 호출로 변경
- **After:** `order-test` — 현재 `CompletableFuture.allOf()` 병렬 구현 유지

### Before 코드 (`benchmark/before-parallel` 브랜치)
```java
// 순차 호출
ProductInfo product = productAdapter.fetch(productId);
UserInfo user = userAdapter.fetch(userId);
CompanyInfo company = companyAdapter.fetch(companyId);
// 총 시간 ≈ 50ms + 50ms + 50ms = 150ms
```

### After 코드 (현재 구현)
```java
// 병렬 호출
CompletableFuture.allOf(productFuture, userFuture, companyFuture).join();
// 총 시간 ≈ max(50ms, 50ms, 50ms) = 50ms
```

### WireMock 스텁 설정
- Product, User, Company 서비스 각각 50ms 지연 응답

### k6 시나리오 (`benchmark/k6/03-feign-parallel.js`)
- VU: 30명
- Duration: 60초
- Endpoint: `POST /api/orders`

### 기대 수치
| 지표 | Before (순차) | After (병렬) | 개선율 |
|------|--------------|-------------|--------|
| 평균 응답시간 | ~150ms | ~50ms | **67% ↓** |
| P95 응답시간 | ~250ms | ~90ms | **64% ↓** |
| TPS | ~80 | ~200 | **150% ↑** |

---

## 수정 대상 파일

| 파일 | 변경 내용 |
|------|-----------|
| `order-service/src/main/resources/application.yaml` | `saga.idempotency.enabled` 플래그 추가 |
| `infrastructure/messaging/handler/IdempotentSagaExecutor.java` | 플래그 기반 Redis 체크 조건 추가 |
| `application/service/OrderFetchService.java` | before 브랜치에서 순차 호출로 변경 |
| `benchmark/seed/insert-orders.sql` | 신규 생성 |
| `benchmark/k6/01-cqrs-index.js` | 신규 생성 |
| `benchmark/k6/02-redis-idempotency.js` | 신규 생성 |
| `benchmark/k6/03-feign-parallel.js` | 신규 생성 |
| `order-service/README.md` | 성능 개선 섹션 추가 |

---

## 검증 절차

1. `docker compose up` 으로 전체 서비스 기동
2. `benchmark/seed/insert-orders.sql` 실행 (10만 건 삽입)
3. Before 브랜치 체크아웃 → k6 실행 → `results/xx-before.json` 저장
4. After 브랜치 체크아웃 → k6 동일 실행 → `results/xx-after.json` 저장
5. 수치 비교 후 `order-service/README.md`에 표 작성
6. `EXPLAIN ANALYZE` 캡처 이미지 첨부

---

## README 결과 구성 예시

```markdown
## 성능 개선 사례

### 1. CQRS + 인덱스 — 조회 성능 96% 개선
| 지표 | Before | After | 개선율 |
|------|--------|-------|--------|
| 평균 응답시간 | 800ms | 30ms | 96% ↓ |
| TPS | 15 | 300 | 1900% ↑ |

### 2. Redis 멱등성 처리 — 중복 소비 99% 차단
| 중복 발송 횟수 | 실제 처리 횟수 (Before) | 실제 처리 횟수 (After) |
|---------------|------------------------|------------------------|
| 100회 | 100회 | 1회 |

### 3. Feign 병렬 호출 — 주문 생성 응답시간 67% 단축
| 지표 | Before (순차) | After (병렬) | 개선율 |
|------|--------------|-------------|--------|
| 평균 응답시간 | 150ms | 50ms | 67% ↓ |
| TPS | 80 | 200 | 150% ↑ |
```
