# Ship-Flow 이벤트 코드 가이드

> **대상**: 각 서비스 담당 팀원
> **목적**: `global` 모듈을 기반으로 이벤트 발행/수신 코드를 바로 작성할 수 있도록 안내

---

## 1. 개요

Ship-Flow는 주문 생성 흐름에서 RabbitMQ 기반 **Saga 패턴**을 사용합니다.
서비스 간 직접 호출 없이 이벤트를 주고받으며 상태를 전이합니다.

### global 모듈이 자동 처리하는 것 (건드리지 않아도 됨)

| 항목 | 클래스 |
|------|--------|
| Exchange / Queue / Binding / DLQ 등록 | `RabbitMqConfig` |
| Jackson 직렬화 설정 | `JacksonConfig` |
| RabbitMQ로 메시지 발행 | `RabbitEventPublisher` |
| 수신 로그 자동 기록 | `AbstractSagaHandler` |

### 팀원이 직접 작성해야 하는 것 (3가지)

1. **이벤트 클래스** — 어떤 데이터를 담아 보낼지
2. **발행 코드** — Service에서 `EventPublisher.publish()` 호출
3. **수신 핸들러** — `AbstractSagaHandler`를 상속해 비즈니스 로직 구현

---

## 2. 서비스 패키지 구조

이벤트 관련 코드는 아래 위치에 작성합니다.

```
{service}/src/main/java/com/shipflow/{servicename}/
├── application/
│   └── {Service}Service.java          ← 이벤트 발행 (EventPublisher 주입)
└── infrastructure/
    └── messaging/
        ├── event/
        │   └── {EventName}Event.java  ← 이벤트 클래스 (SagaEvent 확장)
        └── handler/
            └── {EventName}Handler.java ← 수신 핸들러 (AbstractSagaHandler 확장)
```

**예시 — order-service 기준**

```
order-service/src/main/java/com/shipflow/orderservice/
├── application/
│   └── OrderService.java
└── infrastructure/
    └── messaging/
        ├── event/
        │   └── OrderCreationStartedEvent.java
        └── handler/
            └── ShipmentCreatedHandler.java
```

---

## 3. Step-by-Step 구현 가이드

### Step 1. application.yml — RabbitMQ 연결 설정

> 이벤트를 사용하는 **모든 서비스**에 필수 추가. 없으면 기동 불가.

```yaml
spring:
  application:
    name: order-service  # 서비스명 유지

  rabbitmq:
    host: localhost       # 로컬 개발 시 localhost, Docker Compose 사용 시 컨테이너명(rabbitmq)
    port: 5672
    username: guest
    password: guest
    listener:
      simple:
        retry:
          enabled: true
          max-attempts: 3           # 최대 재시도 횟수
          initial-interval: 1000ms  # 첫 재시도 대기 시간
          multiplier: 2.0           # 지수 백오프: 1s → 2s → 4s
```

---

### Step 2. Application 클래스 — @EnableJpaAuditing 추가

> `BaseEntity`의 `createdAt`, `createdBy` 자동 입력에 필수.

```java
// order-service/src/main/java/com/shipflow/orderservice/OrderserviceApplication.java
@SpringBootApplication
@EnableJpaAuditing  // ← 반드시 추가
public class OrderserviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderserviceApplication.class, args);
    }
}
```

---

### Step 3. 이벤트 클래스 작성 — SagaEvent 확장

> 이벤트를 **발행하는 서비스**에서 작성합니다.
> 발행 서비스와 수신 서비스 모두 같은 클래스가 필요하므로 **`infrastructure/messaging/event/` 에 작성 후 팀원에게 공유**합니다.

```java
// order-service/infrastructure/messaging/event/OrderCreationStartedEvent.java
package com.shipflow.orderservice.infrastructure.messaging.event;

import com.shipflow.global.messaging.event.EventType;
import com.shipflow.global.messaging.event.SagaEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor  // ← Jackson 역직렬화에 필수! 없으면 수신 시 오류 발생
public class OrderCreationStartedEvent extends SagaEvent {

    private UUID orderId;
    private UUID productId;
    private int quantity;

    public OrderCreationStartedEvent(UUID orderId, UUID productId, int quantity) {
        super(EventType.ORDER_CREATION_STARTED);  // ← EventType 상수 사용
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
```

**EventType 상수 목록** (`global/messaging/event/EventType.java`에 정의됨)

| 상수 | 값 |
|------|----|
| `EventType.ORDER_CREATION_STARTED` | `"order.creation.started"` |
| `EventType.ORDER_CREATED` | `"order.created"` |
| `EventType.ORDER_CREATION_FAILED` | `"order.creation.failed"` |
| `EventType.ORDER_CANCELED` | `"order.canceled"` |
| `EventType.PRODUCT_STOCK_DECREASED` | `"product.stock.decreased"` |
| `EventType.PRODUCT_STOCK_DECREASED_FAILED` | `"product.stock.decreased.failed"` |
| `EventType.PRODUCT_STOCK_RESTORED` | `"product.stock.restored"` |
| `EventType.SHIPMENT_CREATED` | `"shipment.created"` |
| `EventType.SHIPMENT_CREATION_FAILED` | `"shipment.creation.failed"` |
| `EventType.SHIPMENT_COMPLETED` | `"shipment.completed"` |

---

### Step 4. 이벤트 발행 — EventPublisher 주입

> `EventPublisher`를 Service에 주입하고 `publish()`를 호출합니다.
> RabbitMQ 연결, 직렬화, Exchange 라우팅은 global 모듈이 자동 처리합니다.

```java
// order-service/application/OrderService.java
package com.shipflow.orderservice.application;

import com.shipflow.global.messaging.publisher.EventPublisher;
import com.shipflow.orderservice.infrastructure.messaging.event.OrderCreationStartedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
 
@Service
@RequiredArgsConstructor
public class OrderService {

    private final EventPublisher eventPublisher;  // ← 인터페이스로 주입

    public void createOrder(UUID productId, int quantity) {
        // 1. 비즈니스 로직 (DB 저장 등)
        UUID orderId = UUID.randomUUID(); // 예시

        // 2. 이벤트 발행 — 이게 전부
        eventPublisher.publish(new OrderCreationStartedEvent(orderId, productId, quantity));
    }
}
```

---

### Step 5. 이벤트 수신 — AbstractSagaHandler 확장

> 이벤트를 **수신하는 서비스**에서 작성합니다.
> `process()` 메서드에 비즈니스 로직만 작성하면 됩니다. 로그는 자동 출력됩니다.

```java
// product-service/infrastructure/messaging/handler/StockDecreaseHandler.java
package com.shipflow.productservice.infrastructure.messaging.handler;

import com.shipflow.global.config.RabbitMqConfig;
import com.shipflow.global.messaging.handler.AbstractSagaHandler;
import com.shipflow.orderservice.infrastructure.messaging.event.OrderCreationStartedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockDecreaseHandler extends AbstractSagaHandler<OrderCreationStartedEvent> {

    private final ProductService productService;

    @RabbitListener(queues = RabbitMqConfig.QUEUE_PRODUCT_ORDER_CREATION_STARTED)  // ← Queue 상수 사용
    @Override
    public void handle(OrderCreationStartedEvent event) {
        super.handle(event);  // ← 반드시 호출 (로그 + process() 실행)
    }

    @Override
    protected void process(OrderCreationStartedEvent event) {
        // 비즈니스 로직만 작성
        productService.decreaseStock(event.getProductId(), event.getQuantity());
    }
}
```

**수신 시 자동 출력되는 로그 예시**

```
[SagaEvent] Received  | type=order.creation.started | eventId=f3a2... | occurredAt=2026-03-31T10:00:00
[SagaEvent] Processed | type=order.creation.started | eventId=f3a2...
// 예외 발생 시:
[SagaEvent] Failed    | type=order.creation.started | eventId=f3a2... | error=재고 부족
```

---

## 4. 이벤트 ↔ 큐 매핑 전체 테이블

`RabbitMqConfig`에 정의된 Queue 상수와 이벤트의 관계입니다.

| 이벤트 (EventType) | Queue 상수 (RabbitMqConfig) | Producer | Consumer |
|--------------------|----------------------------|----------|----------|
| `ORDER_CREATION_STARTED` | `QUEUE_PRODUCT_ORDER_CREATION_STARTED` | Order | Product |
| `PRODUCT_STOCK_DECREASED` | `QUEUE_ORDER_STOCK_DECREASED` | Product | Order |
| `PRODUCT_STOCK_DECREASED_FAILED` | `QUEUE_ORDER_STOCK_DECREASED_FAILED` | Product | Order |
| `PRODUCT_STOCK_RESTORED` | `QUEUE_ORDER_STOCK_RESTORED` | Product | Order |
| `ORDER_CREATED` | `QUEUE_SHIPMENT_ORDER_CREATED` | Order | Shipment |
| `ORDER_CREATED` | `QUEUE_PRODUCT_ORDER_CREATED` | Order | Product |
| `ORDER_CREATED` | `QUEUE_SLACK_ORDER_CREATED` | Order | Notification |
| `ORDER_CREATION_FAILED` | `QUEUE_PRODUCT_ORDER_CREATION_FAILED` | Order | Product |
| `ORDER_CANCELED` | `QUEUE_PRODUCT_ORDER_CANCELED` | Order | Product |
| `ORDER_CANCELED` | `QUEUE_SHIPMENT_ORDER_CANCELED` | Order | Shipment |
| `SHIPMENT_CREATED` | `QUEUE_ORDER_SHIPMENT_CREATED` | Shipment | Order |
| `SHIPMENT_CREATED` | `QUEUE_AI_SHIPMENT_CREATED` | Shipment | Notification(AI) |
| `SHIPMENT_CREATION_FAILED` | `QUEUE_ORDER_SHIPMENT_CREATION_FAILED` | Shipment | Order |
| `SHIPMENT_COMPLETED` | `QUEUE_ORDER_SHIPMENT_COMPLETED` | Shipment | Order |

---

## 5. 주의사항 & FAQ

### Q1. `@NoArgsConstructor`를 빠뜨리면 어떻게 되나요?

수신 서비스에서 Jackson이 역직렬화 시 기본 생성자를 찾지 못해 아래 오류가 발생합니다.

```
com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
No suitable constructor found for type [OrderCreationStartedEvent]
```

→ **이벤트 클래스에 `@NoArgsConstructor` 반드시 추가**

---

### Q2. RabbitMQ가 없으면 서비스가 기동이 안 되나요?

네. `spring-boot-starter-amqp` 의존성이 포함된 서비스는 RabbitMQ 연결을 시도합니다.
로컬 개발 시 Docker로 먼저 기동하세요.

```bash
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3-management
```

Management UI: `http://localhost:15672` (계정: guest / guest)

---

### Q3. 핸들러에서 예외를 던지면 어떻게 되나요?

`application.yml`의 재시도 설정에 따라 동작합니다.

```
예외 발생 → 1초 후 재시도 → 실패 → 2초 후 재시도 → 실패 → 4초 후 재시도 → 실패
→ Dead Letter Queue(saga.events.dlx)로 이동
→ RabbitMQ Management UI(localhost:15672)에서 확인 가능
```

→ **의도적으로 무시하고 싶은 예외**는 `process()` 내부에서 `catch`하고 조용히 처리하세요.
예외를 던지면 재시도가 발생합니다.

---

### Q4. 한 이벤트를 여러 서비스가 수신할 수 있나요?

네. `ORDER_CREATED` 이벤트가 대표적인 예로 Shipment, Product, Notification 세 서비스가 각각 다른 Queue로 수신합니다.
각 서비스는 자신의 Queue 상수만 지정하면 됩니다.

```java
// shipment-service
@RabbitListener(queues = RabbitMqConfig.QUEUE_SHIPMENT_ORDER_CREATED)

// product-service
@RabbitListener(queues = RabbitMqConfig.QUEUE_PRODUCT_ORDER_CREATED)

// notification-service
@RabbitListener(queues = RabbitMqConfig.QUEUE_SLACK_ORDER_CREATED)
```

---

### Q5. 서비스가 이벤트를 발행만 하고 수신은 안 해도 되나요?

네. 발행만 필요하면 `EventPublisher`만 주입하고 핸들러는 작성하지 않아도 됩니다.
반대로 수신만 하는 서비스도 마찬가지입니다.
