# 주문 조회 57% 개선기 — CQRS와 복합 인덱스로 MSA 성능 문제 해결하기

> MSA 물류 배송 플랫폼에서 주문 목록 조회 응답시간을 **19.16ms → 8.18ms (57% 단축)** 한 과정을 공유합니다.
> 문제 발견 → 원인 분석 → 설계 결정 → 구현 → 성과 측정까지 실제 코드와 수치 중심으로 정리했습니다.

---

## 1. 문제 발견

### 서비스 개요

저는 MSA 기반 물류 배송 플랫폼 **ship-flow** 프로젝트에서 주문 서비스 개발을 담당했습니다.
전체 아키텍처는 주문·재고·배송·허브·사용자·업체 등 6개 이상의 마이크로서비스로 구성되어 있습니다.

### 문제 상황

주문 목록 조회 API(`GET /api/orders`)를 구현하면서 한 가지 구조적 문제가 보였습니다.

**주문 목록에 표시해야 할 정보:**
- 주문자 이름 (User 서비스)
- 상품 이름 (Product 서비스)
- 공급사 / 수령사 이름 (Company 서비스)
- 출발 허브 / 도착 허브 이름 (Hub 서비스)
- 배송 상태 (Shipment 서비스)

이 모든 정보가 `p_orders` 테이블 하나에는 ID만 저장돼 있었습니다. 즉, 목록 조회 한 번에 **5개 서비스의 데이터가 필요**한 구조였습니다.

### 부하 테스트 결과

10만 건 데이터 기준으로 K6 부하 테스트를 실행했습니다.

```javascript
// benchmark/k6/01-cqrs-index.js
export let options = {
  vus: 50,       // 동시 사용자 50명
  duration: '30s',
  thresholds: {
    http_req_duration: ['p(95)<5000'],
    http_req_failed:   ['rate<0.01'],
  },
};

export default function () {
  const url = `${BASE_URL}/api/orders?ordererId=${ORDERER_ID}&size=10&page=0&sort=createdAt,DESC`;
  const res = http.get(url, { headers: HEADERS });
  check(res, { 'status is 200': (r) => r.status === 200 });
  sleep(0.1);
}
```

| 지표 | 측정값 |
|------|--------|
| 평균 응답시간 | 19.16ms |
| P95 응답시간 | 32.48ms |
| TPS | 417.73 req/s |

숫자만 보면 느리지 않아 보일 수 있습니다. 하지만 이건 **로컬 환경, 10만 건** 기준입니다.
실제 운영 환경(100만 건 이상, 다중 서버 Feign 호출 지연 포함)을 감안하면 병목이 될 가능성이 높았습니다.
특히 "사용자가 본인 주문 내역을 실시간으로 추적하는 요청"은 쓰기보다 훨씬 빈번하게 발생합니다.

---

## 2. 원인 분석

### 2-1. 기존 테이블 구조의 한계

기존 `p_orders` 테이블(`OrderJpaEntity`)은 다음과 같이 설계돼 있었습니다.

```java
// OrderJpaEntity.java
@Table(
    name = "p_orders",
    indexes = {
        @Index(name = "idx_orders_orderer_id", columnList = "orderer_id")  // 단일 컬럼 인덱스
    }
)
public class OrderJpaEntity extends BaseEntity {
    private UUID ordererId;   // ID만 저장
    private UUID productId;   // ID만 저장
    // CompanyInfo, HubInfo도 ID만 저장
    // 이름, 상태 등 표시용 데이터 없음
}
```

`orderer_id`에 단일 인덱스가 있었지만, 실제 조회 쿼리는 `orderer_id` 필터링 후 `created_at DESC` 정렬까지 수행합니다.

```sql
-- 실제 실행되는 쿼리 패턴
SELECT * FROM p_orders
WHERE orderer_id = ?
  AND deleted_at IS NULL
ORDER BY created_at DESC
LIMIT 10;
```

`orderer_id` 단일 인덱스로는 필터링은 해결되지만, **정렬을 위한 Filesort가 별도로 발생**합니다.
데이터가 많아질수록 이 Filesort 비용이 선형적으로 증가합니다.

### 2-2. MSA 환경의 분산 데이터 문제

더 근본적인 문제는 **MSA 구조에서 데이터가 여러 서비스에 분산**돼 있다는 점입니다.

```
주문 목록 조회 1회 = p_orders 조회
                   + User 서비스 Feign 호출 (이름 조회)
                   + Product 서비스 Feign 호출 (상품명 조회)
                   + Company 서비스 Feign 호출 x2 (공급사, 수령사)
                   + Hub 서비스 Feign 호출 x2 (출발 허브, 도착 허브)
```

페이지당 10건을 조회하면 최대 **61회의 I/O**가 발생하는 구조였습니다.

---

## 3. 해결 전략 — CQRS 도입 결정

### 3-1. 왜 캐싱이 아닌 CQRS인가?

처음에는 Redis 캐싱을 먼저 고려했습니다. 하지만 MSA 환경에서 캐싱에는 몇 가지 문제가 있었습니다.

| 구분 | Redis 캐싱 | CQRS ReadModel |
|------|-----------|----------------|
| 정합성 | 캐시 무효화 전략 복잡 | 이벤트 기반 동기화로 명확 |
| 구현 복잡도 | 분산 캐시 + TTL 관리 | 테이블 + 이벤트 핸들러 |
| 조회 성능 | 캐시 히트 시 O(1) | 인덱스 스캔으로 안정적 |
| 콜드 스타트 | 캐시 미스 시 원래 성능으로 복귀 | 항상 일정한 성능 |

특히 주문 데이터는 **배송 상태 변경, 주문 확정 등 이벤트가 잦아** 캐시 무효화가 복잡해집니다.
MSA 환경에서 여러 서비스에 걸친 분산 캐시 일관성 보장은 별도의 인프라가 필요합니다.

CQRS ReadModel은 **쓰기 발생 시 읽기 모델을 함께 업데이트**하는 방식이므로, 캐시 무효화 문제가 구조적으로 해결됩니다.

### 3-2. CQRS 아키텍처 설계

```
┌─────────────────────────────────────────────────────────┐
│                      클라이언트                          │
│          주문 생성/수정/취소          주문 목록 조회       │
└────────────────┬──────────────────────────┬─────────────┘
                 ↓                          ↓
    ┌────────────────────┐      ┌──────────────────────┐
    │ OrderCommandService│      │  OrderQueryService   │
    │  (쓰기 전용)        │      │  (읽기 전용)          │
    │  @Transactional    │      │  @Transactional      │
    └────────┬───────────┘      │  (readOnly = true)   │
             │                  └──────────┬───────────┘
             │ save                        │ search
             ↓                            ↓
    ┌──────────────┐          ┌────────────────────────┐
    │   p_orders   │          │  p_order_read_models   │
    │  (원본, 정규화)│          │  (ReadModel, 비정규화)  │
    │              │          │                        │
    │  orderer_id  │          │  orderer_id (indexed)  │
    │  product_id  │          │  orderer_name ←────── 비정규화
    │  company_id  │          │  product_name ←────── 비정규화
    └──────────────┘          │  company_name ←────── 비정규화
             │                └────────────────────────┘
             │ ApplicationEventPublisher
             ↓
    ┌──────────────────────────────────────────┐
    │         OrderProjectionService            │
    │  @TransactionalEventListener              │
    │  (phase = BEFORE_COMMIT)                 │
    │  원본 트랜잭션과 같은 트랜잭션 내 동기화  │
    └──────────────────────────────────────────┘
```

핵심 아이디어는 두 가지입니다:
1. **비정규화(Denormalization):** 조회 시 필요한 모든 데이터를 ReadModel 테이블에 미리 저장
2. **이벤트 기반 동기화:** 원본 데이터 변경 시 도메인 이벤트를 발행해 ReadModel을 즉시 업데이트

---

## 4. 핵심 구현

### 4-1. ReadModel 테이블 + 복합 인덱스

**비정규화 설계**

기존 `p_orders`와 달리 `p_order_read_models`는 조회 화면에 필요한 **모든 정보를 하나의 테이블**에 담습니다.

```java
// OrderReadModelJpaEntity.java
@Entity
@Table(
    name = "p_order_read_models",
    indexes = {
        // 복합 인덱스: "특정 사용자의 최신 주문 정렬" 쿼리 패턴 최적화
        @Index(name = "idx_read_models_orderer_created",
               columnList = "orderer_id, created_at DESC")
    }
)
public class OrderReadModelJpaEntity {

    @Id
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    // 배송 정보 (배송 서비스 데이터를 비정규화)
    private UUID shipmentId;
    @Enumerated(EnumType.STRING)
    private ShipmentStatus shipmentStatus;

    // 업체 이름까지 저장 (Company 서비스 Feign 호출 불필요)
    private UUID supplierCompanyId;
    private String supplierCompanyName;  // ← 비정규화 핵심

    private UUID receiverCompanyId;
    private String receiverCompanyName;  // ← 비정규화 핵심

    // 주문자 이름까지 저장 (User 서비스 Feign 호출 불필요)
    private UUID ordererId;
    private String ordererName;          // ← 비정규화 핵심

    // 상품 이름까지 저장 (Product 서비스 Feign 호출 불필요)
    private UUID productId;
    private String productName;          // ← 비정규화 핵심

    // 허브 이름까지 저장 (Hub 서비스 Feign 호출 불필요)
    private UUID departureHubId;
    private String departureHubName;    // ← 비정규화 핵심

    private UUID arrivalHubId;
    private String arrivalHubName;      // ← 비정규화 핵심

    // ... 기타 필드
}
```

**복합 인덱스 설계 이유**

`(orderer_id, created_at DESC)` 순서가 핵심입니다.

실제 조회 쿼리 패턴을 분석하면:
```sql
WHERE orderer_id = ?          -- orderer_id 동등 비교
ORDER BY created_at DESC      -- created_at 역순 정렬
LIMIT 10;
```

복합 인덱스 덕분에 **인덱스 내에서 이미 정렬된 결과를 순서대로 읽기**만 하면 됩니다.
Filesort 없이 Index Scan만으로 처리됩니다.

| 구분 | p_orders (Command) | p_order_read_models (Query) |
|------|-------------------|-----------------------------|
| 역할 | 쓰기 전용 정규화 데이터 | 읽기 전용 비정규화 데이터 |
| 인덱스 | `orderer_id` 단일 | `(orderer_id, created_at DESC)` 복합 |
| JOIN/Feign | 조회 시 필요 | 불필요 |
| 데이터 중복 | 없음 | 있음 (이름 등) |

### 4-2. @TransactionalEventListener BEFORE_COMMIT 동기화

**이벤트 발행 — OrderCommandService**

주문 관련 작업 시 `ApplicationEventPublisher`로 도메인 이벤트를 발행합니다.

```java
// OrderCommandService.java
@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandService {

    private final OrderRepository orderRepository;
    private final EventPublisher rabbitPublisher;                    // 외부 서비스 연동용 (RabbitMQ)
    private final ApplicationEventPublisher domainEventPublisher;   // ReadModel 동기화용 (내부)

    public OrderResult createOrder(CreateOrderRequest request, UUID ordererId) {
        // 1. Feign으로 외부 서비스 데이터 수집 (이름, 허브 정보 등)
        CreateOrderCommand cmd = orderFetchService.fetchAndBuild(
            ordererId, request.productId(), request.quantity(),
            request.requestDeadline(), request.requestNote()
        );

        // 2. 원본 주문 저장
        Order order = Order.create(...);
        Order saved = orderRepository.save(order);

        // 3. 도메인 이벤트 발행 (이름 정보도 함께 전달)
        domainEventPublisher.publishEvent(new OrderCreatingEvent(
            saved.getId(), saved.getOrdererId(), cmd.ordererName(),   // ← ordererName 포함
            saved.getProductId(), cmd.productName(),                   // ← productName 포함
            saved.getCompanyInfo().getSupplierCompanyId(), cmd.supplierCompanyName(),
            // ... 비정규화에 필요한 모든 정보
        ));

        // 4. RabbitMQ로 Saga 이벤트 발행 (재고 감소 요청 등 외부 연동)
        rabbitPublisher.publish(new OrderCreationStartedEvent(...));

        return OrderResult.from(saved);
    }
}
```

**이벤트 수신 — OrderProjectionService**

```java
// OrderProjectionService.java
@Service
@RequiredArgsConstructor
public class OrderProjectionService {

    private final OrderReadModelRepository readModelRepository;

    // BEFORE_COMMIT: 원본 트랜잭션 커밋 직전에 실행
    // → 원본이 롤백되면 ReadModel 업데이트도 함께 롤백 (데이터 정합성 보장)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(OrderCreatingEvent e) {
        readModelRepository.save(OrderReadModel.builder()
            .orderId(e.orderId())
            .orderStatus(OrderStatus.CREATING)
            .ordererId(e.ordererId())
            .ordererName(e.ordererName())         // 이벤트에서 받은 이름 저장
            .productId(e.productId())
            .productName(e.productName())         // 이벤트에서 받은 이름 저장
            .supplierCompanyId(e.supplierCompanyId())
            .supplierCompanyName(e.supplierCompanyName())
            .receiverCompanyId(e.receiverCompanyId())
            .receiverCompanyName(e.receiverCompanyName())
            .departureHubId(e.departureHubId())
            .arrivalHubId(e.arrivalHubId())
            // ... 나머지 필드
            .build());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(OrderConfirmedEvent e) {
        // toBuilder()로 기존 ReadModel에서 변경된 필드만 업데이트
        readModelRepository.save(find(e.orderId()).toBuilder()
            .orderStatus(OrderStatus.CREATED)
            .productName(e.productName())
            .build());
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(OrderShipmentLinkedEvent e) {
        // 배송 연결 시 배송 정보도 ReadModel에 동기화
        readModelRepository.save(find(e.orderId()).toBuilder()
            .shipmentId(e.shipmentId())
            .shipmentStatus(e.shipmentStatus())
            .departureHubId(e.departureHubId())
            .departureHubName(e.departureHubName())
            .arrivalHubId(e.arrivalHubId())
            .arrivalHubName(e.arrivalHubName())
            .build());
    }

    // 취소, 완료, 삭제 등 8가지 이벤트 핸들러 동일 패턴
}
```

**왜 BEFORE_COMMIT인가?**

```
BEFORE_COMMIT 선택 시:
  트랜잭션 시작
    → p_orders 저장
    → OrderCreatingEvent 발행
    → [BEFORE_COMMIT] p_order_read_models 저장  ← 같은 트랜잭션
  트랜잭션 커밋 (또는 롤백)

  만약 커밋 실패 → p_orders, p_order_read_models 모두 롤백 ✅

AFTER_COMMIT 선택 시:
  트랜잭션 시작
    → p_orders 저장
  트랜잭션 커밋 ✅ (p_orders 영속)
    → [AFTER_COMMIT] p_order_read_models 저장  ← 별도 트랜잭션

  만약 AFTER_COMMIT 실패 → p_orders는 저장됐지만 ReadModel은 누락 ❌
```

BEFORE_COMMIT은 원본과 ReadModel의 **원자성을 트랜잭션으로 보장**합니다.
단점은 Command 트랜잭션에 Projection 비용이 추가된다는 점입니다. 이 트레이드오프는 뒤에서 다시 다룹니다.

### 4-3. OrderQueryService — 조회 최적화

```java
// OrderQueryService.java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)  // 읽기 전용: 스냅샷 불필요, flush 생략
public class OrderQueryService {

    // 허용된 페이지 크기만 허용 (임의의 값으로 대량 조회 방지)
    private static final Set<Integer> ALLOWED_PAGE_SIZES = Set.of(10, 30, 50);
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "updatedAt");

    /**
     * 읽기 최적화된 주문 모델을 검색합니다.
     * JOIN 없이 배송 정보를 포함한 데이터를 한 번에 가져옵니다.
     */
    public Slice<OrderReadModel> searchOrders(OrderSearchCondition condition, Pageable pageable) {
        // 페이지 크기 정규화 (10/30/50 이외 → 10으로 fallback)
        int pageSize = ALLOWED_PAGE_SIZES.contains(pageable.getPageSize())
            ? pageable.getPageSize() : 10;

        // 정렬 필드 화이트리스트 검증 (임의 컬럼 정렬 방지)
        Sort sort = pageable.getSort().isSorted()
            && pageable.getSort().stream().allMatch(o -> ALLOWED_SORT_FIELDS.contains(o.getProperty()))
            ? pageable.getSort() : DEFAULT_SORT;

        Pageable normalized = PageRequest.of(pageable.getPageNumber(), pageSize, sort);
        return orderReadModelRepository.search(condition, normalized);
    }
}
```

**QueryDSL 기반 동적 쿼리 (Slice 패턴)**

```java
// OrderReadModelQueryRepositoryImpl.java
@Override
public Slice<OrderReadModel> search(OrderSearchCondition condition, Pageable pageable) {
    QOrderReadModelJpaEntity q = QOrderReadModelJpaEntity.orderReadModelJpaEntity;
    BooleanBuilder builder = new BooleanBuilder();

    // 소프트 딜리트 자동 제외
    builder.and(q.deletedAt.isNull());

    // 동적 필터 (null이면 조건 추가 안 함)
    if (condition.ordererId() != null)  builder.and(q.ordererId.eq(condition.ordererId()));
    if (condition.status() != null)     builder.and(q.orderStatus.eq(condition.status()));
    if (condition.productId() != null)  builder.and(q.productId.eq(condition.productId()));
    // ... 기타 조건

    // Slice 패턴: pageSize+1개 조회 후 hasNext 판단 (COUNT 쿼리 불필요)
    List<OrderReadModelJpaEntity> rows = queryFactory
        .selectFrom(q)
        .where(builder)
        .orderBy(toOrderSpecifiers(pageable.getSort(), q))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize() + 1L)  // +1로 다음 페이지 존재 여부 판단
        .fetch();

    boolean hasNext = rows.size() > pageable.getPageSize();
    if (hasNext) rows = rows.subList(0, pageable.getPageSize());

    return new SliceImpl<>(rows.stream().map(OrderReadModelJpaEntity::toDomain).toList(),
                           pageable, hasNext);
}
```

Slice 패턴은 `COUNT(*)` 쿼리를 생략하므로 무한 스크롤이나 "더 보기" UI에 적합합니다.

---

## 5. 부록: Feign 병렬 처리

주문 생성 시 Product, User, Company 3개 서비스를 **순차적으로 Feign 호출**하면 각 서비스 응답시간이 누적됩니다.
Product(50ms) + User(50ms) + Company(50ms) = 약 150ms 직렬 처리.

서비스 간 의존성을 분석해보면:
- Product 조회와 User 조회는 **서로 독립적** → 병렬 가능
- Company 조회는 User의 `receiverCompanyId` 결과에 **의존** → 순차 필요

```java
// OrderFetchService.java
public CreateOrderCommand fetchAndBuild(UUID ordererId, UUID productId,
                                        int quantity, LocalDateTime deadline, String note) {
    // Step 1: Product + User 병렬 호출 (서로 의존성 없음)
    CompletableFuture<ProductInfo> productFuture = CompletableFuture.supplyAsync(
        () -> productAdapter.fetch(ordererId.toString(), productId, quantity)
    );
    CompletableFuture<UserInfo> userFuture = CompletableFuture.supplyAsync(
        () -> userAdapter.fetch(ordererId)
    );

    try {
        CompletableFuture.allOf(productFuture, userFuture).join();  // 둘 다 완료될 때까지 대기
    } catch (CompletionException e) {
        // CompletionException 언래핑: 실제 비즈니스 예외 복원
        Throwable cause = e.getCause();
        if (cause instanceof RuntimeException re) throw re;
        if (cause instanceof Error err) throw err;
        throw e;
    }

    ProductInfo product = productFuture.join();
    UserInfo user = userFuture.join();

    // Step 2: User 결과의 receiverCompanyId로 Company 호출 (의존성 있어 순차)
    ReceiverCompanyInfo company = companyAdapter.fetch(user.receiverCompanyId());

    return new CreateOrderCommand(
        ordererId, user.ordererName(), productId, product.productName(),
        product.supplierCompanyId(), product.supplierCompanyName(),
        // ...
    );
}
```

순차(~150ms) → 병렬(~50ms + Company 50ms = ~100ms) 수준으로 개선됩니다.

---

## 6. 성과 측정

10만 건 데이터, VU 50명, 30초 K6 부하 테스트 기준입니다.

| 지표 | Before (인덱스 없음) | After (CQRS + 복합 인덱스) | 개선율 |
|------|---------------------|---------------------------|--------|
| **평균 응답시간** | 19.16ms | 8.18ms | **57.3% 단축** |
| **P95 응답시간** | 32.48ms | 12.35ms | **62.0% 단축** |
| **TPS** | 417.73 req/s | 460.90 req/s | **10.3% 향상** |

```
Before:
  avg=19.16ms  p(95)=32.48ms  req/s=417.73

After:
  avg= 8.18ms  p(95)=12.35ms  req/s=460.90
       └─ 57% ↓        └─ 62% ↓      └─ 10% ↑
```

P95 응답시간이 62% 단축됐다는 것은 **대량 조회 환경에서도 일관된 빠른 속도**를 보장할 수 있다는 의미입니다.

---

## 7. 트레이드오프와 배운 점

### BEFORE_COMMIT의 단점

BEFORE_COMMIT 방식은 Command 트랜잭션 내에서 Projection이 동기적으로 실행됩니다.
주문 생성 1회에 `p_orders` 저장 + `p_order_read_models` 저장이 함께 발생합니다.

트래픽이 더 커지면 AFTER_COMMIT + 보상 트랜잭션(또는 Outbox 패턴) 전환을 고려해야 합니다.

```
현재 (BEFORE_COMMIT, 동기):
  Command 처리시간 = 비즈니스 로직 + Projection 비용

미래 (AFTER_COMMIT + Outbox, 비동기):
  Command 처리시간 = 비즈니스 로직만
  Projection 처리시간 = 별도 (결과적 일관성 허용)
```

### 비정규화의 단점

원본 데이터(사용자 이름, 상품명 등)가 변경될 경우 ReadModel도 업데이트해야 합니다.
현재는 이벤트 핸들러가 8가지(생성/확정/배송연결/수정/완료/취소/실패/삭제)이며,
서비스가 커질수록 핸들러 관리 복잡도가 증가합니다.

### 핵심 배움

> **"읽기 패턴을 먼저 분석하고 테이블을 설계해야 한다."**

CQRS를 도입하기 전, 가장 먼저 한 일은 "이 API가 어떤 데이터를, 어떤 순서로, 얼마나 자주 조회하는가"를 정리한 것입니다.

조회 조건과 정렬 기준이 결정되면 인덱스 설계가 따라옵니다.
`(orderer_id, created_at DESC)` 복합 인덱스는 "사용자별 최신 주문 목록" 쿼리 패턴을 정확히 반영한 결과입니다.

MSA에서 마이크로서비스 경계를 넘어오는 JOIN은 Feign 호출로 대체되며,
이 비용은 생각보다 크고, CQRS ReadModel로의 비정규화가 현실적인 해결책이 됩니다.

---

## 마치며

| 주제 | 핵심 |
|------|------|
| CQRS | Command/Query 분리 + ReadModel 비정규화로 조회 전용 최적화 |
| 복합 인덱스 | 쿼리 패턴 분석 → `(orderer_id, created_at DESC)` Filesort 제거 |
| BEFORE_COMMIT | 원본-ReadModel 트랜잭션 원자성 보장 |
| Feign 병렬화 | 의존성 분석 후 CompletableFuture 병렬 처리 |

성능 최적화는 측정에서 시작합니다.
K6로 현재 성능을 수치화하고, 병목의 원인을 구조적으로 분석한 뒤, 가장 적합한 패턴을 선택하는 과정이 핵심입니다.

---

**GitHub:** [https://github.com/kim-jun-won/ship-flow](https://github.com/kim-jun-won/ship-flow)
**테스트 환경:** 10만 건 데이터, 로컬 환경, K6 v0.49+, Spring Boot 3.x, Java 21, PostgreSQL
