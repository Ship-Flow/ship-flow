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
