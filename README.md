# 🚚 Ship-Flow - MSA 물류 관리 시스템

## 🧭 목차
- [🚀 프로젝트 소개](#-프로젝트-소개)
- [🎯 핵심 기능](#-핵심-기능)
- [⚙️ 실행 방법](#️-실행-방법)
- [🛠️ 기술 스택](#️-기술-스택)
- [🏗️ 시스템 구조](#️-시스템-구조)
- [👥 팀 역할 분담](#-팀-역할-분담)

---

## 📅 프로젝트 기간
- 2026.03.30 ~ 2026.04.08

---

## 🚀 프로젝트 소개

**ShipFlow**는
허브 중심 물류 구조를 기반으로 **주문 → 배송 → 발송 관리까지 처리하는 MSA 기반 물류 시스템**입니다.
주문이 생성되면 허브 간 배송 경로를 계산하고 배송을 생성하며,
이후 발송 시점 관리와 알림까지 이어지는 전체 물류 흐름을 처리합니다.

### 📌 프로젝트 목표
- MSA 구조 기반 서비스 분리 및 협업 경험
- 이벤트 기반 아키텍처 (RabbitMQ + Saga 패턴) 적용
- 공통 규약 기반 백엔드 설계
- 실제 서비스 흐름을 고려한 물류 시스템 구현

---

## 🎯 핵심 기능

### 📦 물류 / 배송
- 허브 및 허브 간 이동정보 관리 (Redis 캐싱 적용)
- 업체 / 상품 / 주문 관리
- 배송 생성 및 배송 경로 계산
- 배송 담당자 배정 및 관리

### 👤 사용자 / 인증
- 승인 기반 회원가입 및 사용자 관리
- Keycloak + JWT 기반 인증 / 인가 (Gateway 처리)

### 🔔 알림
- Slack Java SDK 기반 DM / 채널 메시지 발송
- Google Gemini API 기반 최종 발송 시한 자동 계산
- 주문 생성 시 RabbitMQ 이벤트 수신 후 발송 허브 담당자에게 자동 알림

### 🔄 공통 기능
- Soft Delete (`deleted_at`, `deleted_by`)
- 검색 / 정렬 / 페이지네이션 (10 / 30 / 50)
- Swagger 기반 API 문서화 (서비스별 제공)

### 🤖 아키텍처 특징
- RabbitMQ 기반 이벤트 통신 + Saga 패턴 적용
- Redis 기반 이벤트 멱등성 처리 (중복 이벤트 방지)
- 서비스 간 내부 API (`/internal`) 구조 분리

---

## ⚙️ 실행 방법

```bash
# 1. .env 파일 생성
cp .env.example .env
# .env 파일에 환경변수 설정 (DB, RabbitMQ, Keycloak, Slack, Gemini API Key 등)

# 2. Docker Compose로 전체 실행
docker-compose up -d
```

### 실행 포트 안내
| 서비스 | 포트 |
| --- | --- |
| Gateway | 8000 |
| Eureka (Discovery) | 8761 |
| Keycloak | 9001 |
| RabbitMQ Management | 15672 |
| PostgreSQL | 5432 |
| Redis | 6379 |

> 모든 비즈니스 서비스는 내부적으로 8080을 사용하며, **Gateway(8000)를 통해 접근**합니다.

### API 문서 (Swagger)

Swagger는 일부 서비스에 적용되어 있습니다.

| 서비스 | URL |
| --- | --- |
| Hub Service | `http://localhost:8000/api/hubs/swagger-ui.html` |
| Notification Service | `http://localhost:8000/api/ai/swagger-ui.html` |

---

## 🛠️ 기술 스택

| 구분 | 기술 |
| --- | --- |
| Backend | Spring Boot 3.x, Java 21, JPA (QueryDSL), FeignClient |
| Security | Keycloak, JWT, OAuth2 Resource Server |
| Architecture | MSA, Event-Driven, Saga Pattern |
| DB | PostgreSQL |
| Cache | Redis |
| Messaging | RabbitMQ |
| Infra | Docker, Docker Compose |
| Gateway | Spring Cloud Gateway, Spring Cloud Eureka |
| Docs | Swagger (SpringDoc) |
| AI | Google Gemini API |
| Slack | Slack Java SDK (`com.slack.api`) |
| Tools | GitHub, Notion |

---

## 🏗️ 시스템 구조

### 서비스 구성

| 서비스 | 설명 |
| --- | --- |
| `gateway-server` | 외부 요청 라우팅, JWT 인증 처리 |
| `discovery-server` | Eureka 서비스 디스커버리 |
| `user-service` | 사용자 관리, Keycloak 연동 |
| `hub-service` | 허브 및 허브 간 이동정보 관리 (Redis 캐싱) |
| `company-service` | 업체 관리 |
| `product-service` | 상품 관리 |
| `order-service` | 주문 관리 |
| `shipment-service` | 배송 및 배송 경로 관리 |
| `notification-service` | Slack 알림 + Gemini AI 발송 시한 계산 |
| `common` | 공통 모듈 (BaseEntity, Saga, RabbitMQ, Exception 등) |

### 통신 방식
- **외부 요청** → Gateway (8000)
- **서비스 간 동기 통신** → REST / FeignClient
- **서비스 간 비동기 통신** → RabbitMQ (Saga 패턴)
- **이벤트 멱등성 처리** → Redis (`saga:processed:{eventId}`)

### 주요 이벤트 흐름 (Saga Pattern)

모든 서비스 간 이벤트는 **RabbitMQ Topic Exchange**를 통해 흐르며,
각 서비스는 **DLQ(Dead Letter Queue)** 와 **Redis 기반 멱등성 처리**로 신뢰성을 보장합니다.

#### 📦 주문 생성 Saga (정상 흐름)
```
[Order Service]
  → order.creation.started 발행

[Product Service] order.creation.started 수신
  → 재고 차감 시도
  → product.stock.decreased 발행

[Shipment Service] product.stock.decreased 수신
  → 배송 + 배송 경로 생성
  → shipment.created 발행

[Order Service] shipment.created 수신
  → 주문 상태 CONFIRMED 업데이트
  → order.created 발행

[Notification Service] shipment.created 수신 (별도 큐)
  → Gemini API: 최종 발송 시한 계산
  → Slack: 발송 허브 담당자에게 알림 발송
```

#### 🔁 주문 생성 실패 보상 흐름 (Rollback)
```
[Product Service] 재고 부족 → product.stock.decreased.failed 발행

[Order Service] product.stock.decreased.failed 수신
  → 주문 상태 FAILED 처리
  → order.creation.failed 발행

[Product Service] order.creation.failed 수신
  → 재고 복원 처리
```

#### ❌ 주문 취소 흐름
```
[Order Service] 주문 취소 요청
  → order.canceled 발행

[Product Service] order.canceled 수신
  → 재고 복원 처리

[Shipment Service] order.canceled 수신
  → 배송 Soft Delete 처리
```

#### ✅ 배송 완료 흐름
```
[Shipment Service] 배송 완료 처리
  → shipment.completed 발행

[Order Service] shipment.completed 수신
  → 주문 상태 COMPLETED 업데이트
```

---

## 👥 팀 역할 분담

| 이름 | 담당 |
| --- | --- |
| 안정후 | Hub Service / Hub Route (경로 탐색) |
| 여정진 | Company Service / Product Service |
| 김준원 | Order Service |
| 이현수 | Shipment Service |
| 김지원 | User Service / Keycloak 인증 |
| 이호영 | Notification Service (AI + Slack) |

---

## 📌 설계 특징

- **도메인 기반 서비스 분리 (MSA)**: 각 서비스 독립 배포 및 독립 DB 스키마
- **Gateway + Keycloak + Eureka**: 인증/인가와 서비스 디스커버리 중앙화
- **RabbitMQ + Saga 패턴**: 분산 트랜잭션 및 서비스 간 비동기 이벤트 처리
- **Redis 이중 활용**: 허브 정보 캐싱 + 이벤트 멱등성 처리
- **Soft Delete 공통 적용**: 모든 엔티티에 `deleted_at`, `deleted_by` 관리
- **검색 / 정렬 / 페이지네이션 표준화**: 10 / 30 / 50건 페이지 공통 모듈 적용
- **내부 API (`/internal`) 구조 분리**: 서비스 간 호출과 외부 요청 구분
- **Slack Java SDK**: Webhook 방식이 아닌 SDK 기반 DM / 채널 메시지 발송

---

## 🚀 한 줄 소개

**주문부터 배송, AI 기반 발송 시한 계산, Slack 자동 알림까지 처리하는 MSA 기반 물류 시스템**
