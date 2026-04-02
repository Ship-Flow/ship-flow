# API 테스트 전략 설계

**Date:** 2026-04-01
**Scope:** order-service
**Status:** Approved

---

## Context

현재 ship-flow 프로젝트는 7개 마이크로서비스로 구성되어 있으며, order-service에만 실제 API가 구현되어 있다. 전체 테스트 커버리지는 사실상 0에 가까운 상태(`contextLoads()` stub만 존재)이며, CQRS 패턴과 RabbitMQ Saga 패턴이 적용된 복잡한 아키텍처에 대한 회귀 테스트가 부재하다.

이 설계는 order-service의 API 테스트를 계층별로 단계적으로 구축하는 전략을 정의한다.

---

## 테스트 전략: 3단계 계층별 접근

### 단계별 개요

```
1단계: 컨트롤러 단위 테스트  (빠른 API 계약 검증)
       ↓
2단계: 서비스 단위 테스트    (비즈니스 로직 검증)
       ↓
3단계: 통합 테스트           (전체 흐름 E2E 검증)
```

---

## 1단계: 컨트롤러 단위 테스트

### 도구
- `@WebMvcTest` — 웹 레이어만 로드 (서비스/DB 불필요)
- `MockMvc` — HTTP 요청/응답 시뮬레이션
- `Mockito` — OrderCommandService, OrderQueryService 모킹

### 대상 파일
- `presentation/controller/OrderController.java`
- `presentation/controller/OrderInternalController.java`

### 테스트 케이스 목록

#### OrderController (`/api/orders`)

| 메서드 | 엔드포인트 | 성공 케이스 | 실패 케이스 |
|---|---|---|---|
| POST | `/api/orders` | 201 + 생성된 주문 반환 | 400 (필수 필드 누락) |
| GET | `/api/orders` | 200 + 리스트 반환 | - |
| GET | `/api/orders/{id}` | 200 + 주문 상세 반환 | 404 (없는 ID) |
| PATCH | `/api/orders/{id}` | 200 + 변경된 주문 반환 | 400, 404 |
| POST | `/api/orders/{id}/cancel` | 200 + 취소된 주문 반환 | 404 |
| DELETE | `/api/orders/{id}` | 204 | 404 |

#### OrderInternalController (`/internal/orders`)

| 메서드 | 엔드포인트 | 성공 케이스 | 실패 케이스 |
|---|---|---|---|
| POST | `/internal/orders/prepare` | 200 | 400 |
| PATCH | `/internal/orders/{id}/confirm` | 200 | 404 |
| PATCH | `/internal/orders/{id}/fail` | 200 | 404 |
| PATCH | `/internal/orders/{id}/cancel` | 200 | 404 |
| PATCH | `/internal/orders/{id}/complete` | 200 | 404 |
| GET | `/internal/orders/{id}` | 200 | 404 |
| GET | `/internal/orders/{id}/read-model` | 200 | 404 |

### 검증 포인트
- HTTP 상태 코드
- 응답 JSON 필드 존재 여부
- `@Valid` 유효성 검사 실패 시 400 반환
- 예외 핸들러 동작 확인

---

## 2단계: 서비스 단위 테스트

### 도구
- `@ExtendWith(MockitoExtension.class)` — 스프링 컨텍스트 없이 순수 Mockito
- 모킹 대상: `OrderRepository`, `OrderReadModelRepository`, 이벤트 퍼블리셔

### 대상 파일
- `application/service/OrderCommandService.java`
- `application/service/OrderQueryService.java`

### 검증 포인트

#### OrderCommandService
- 주문 생성 → `OrderRepository.save()` 호출 여부
- 주문 상태 전이 검증 (CREATING → CREATED → COMPLETED 등)
- 잘못된 상태 전이 시 예외 발생 여부
- `OrderReadModel` 업데이트 시점 확인 (linkShipment 호출 시)
- 존재하지 않는 orderId 조회 시 예외 발생

#### OrderQueryService
- `getOrder()` → `OrderRepository.findById()` 호출 후 `OrderResult` 변환
- `getReadModel()` → `OrderReadModelRepository.findById()` 호출
- 결과 없을 시 예외 발생

---

## 3단계: 통합 테스트

### 도구
- `@SpringBootTest(webEnvironment = RANDOM_PORT)` — 전체 애플리케이션 로드
- `TestContainers` — 실제 PostgreSQL + RabbitMQ 컨테이너

### 의존성 추가 (order-service/build.gradle)

```gradle
testImplementation 'org.testcontainers:junit-jupiter'
testImplementation 'org.testcontainers:postgresql'
testImplementation 'org.testcontainers:rabbitmq'
```

### 공통 설정 클래스
```java
// AbstractIntegrationTest.java
@SpringBootTest
@Testcontainers
abstract class AbstractIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3-management");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        // ...
    }
}
```

### 검증 포인트
- 주문 생성 API 호출 → DB `p_orders` 테이블 저장 확인
- 주문 조회 API → DB에서 정확한 데이터 반환
- 상태 변경 메시지 수신 → DB 상태 업데이트 확인
- `p_order_read_models` 비정규화 테이블 동기화 확인

---

## 파일 구조 (order-service)

```
src/test/java/com/shipflow/orderservice/
├── presentation/
│   ├── OrderControllerTest.java
│   └── OrderInternalControllerTest.java
├── application/
│   ├── OrderCommandServiceTest.java
│   └── OrderQueryServiceTest.java
└── integration/
    ├── AbstractIntegrationTest.java
    └── OrderIntegrationTest.java
```

---

## 검증 방법

1. `./gradlew :order-service:test` — 전체 테스트 실행
2. `./gradlew :order-service:test --tests "*.OrderControllerTest"` — 컨트롤러만 실행
3. 테스트 리포트: `order-service/build/reports/tests/test/index.html`

---

## 제외 범위

- shipment-service 및 기타 서비스 (API 미구현)
- E2E 멀티서비스 시나리오 (서비스 간 Feign 호출 흐름)
- 성능/부하 테스트
