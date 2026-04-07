# 🚚 Ship-Flow - MSA 물류 관리 시스템

## 🧭 목차

- 🚀 프로젝트 소개
- 🎯 핵심 기능
- ⚙️ 실행 방법
- 🛠️ 기술 스택
- 🏗️ 시스템 구조
- 👥 팀 역할 분담
- 📄 API 문서

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
- 이벤트 기반 아키텍처 적용
- 공통 규약 기반 백엔드 설계
- 실제 서비스 흐름을 고려한 물류 시스템 구현

---

## 🎯 핵심 기능

### 📦 물류 / 배송

- 허브 및 허브 간 이동정보 관리
- 업체 / 상품 / 주문 관리
- 배송 생성 및 배송 경로 계산
- 배송 담당자 배정 및 관리

### 👤 사용자 / 인증

- 회원가입 승인 기반 사용자 관리
- JWT 기반 인증 / 인가 (Gateway 처리)

### 🔄 공통 기능

- Soft Delete (`deleted_at`)
- 검색 / 정렬 / 페이지네이션 (10 / 30 / 50)
- Swagger 기반 API 문서화

### 🤖 확장 기능

- 이벤트 기반 서비스 간 통신 (RabbitMQ)
- 발송 시점 계산 및 자동 알림 처리
- 서비스 간 내부 API (`/internal`) 분리

---

## ⚙️ 실행 방법

```bash
# 1. .env 파일 생성
cp .env.example .env
# .env 파일에 환경변수 설정

# 2. Docker Compose로 실행
docker-compose up -d
```

## 🛠️ 기술 스택

| 구분 | 기술 |
| --- | --- |
| Backend | Spring Boot 3.x, Java 21, JPA |
| Security | Spring Security, JWT |
| Architecture | MSA, Event Driven, Saga |
| DB | PostgreSQL |
| Messaging | RabbitMQ |
| Infra | Docker |
| Gateway | Spring Cloud Gateway, Eureka |
| Docs | Swagger |
| Tools | GitHub, Notion |

---

## 🏗️ 시스템 구조

### 서비스 구성

- Gateway
- Eureka
- User Service
- Hub Service
- Company / Product Service
- Order Service
- Shipment Service
- AI Service
- Slack Service

### 통신 방식

- 외부 요청 → Gateway
- 서비스 간 통신 → REST / Feign
- 이벤트 통신 → RabbitMQ

---

## 👥 팀 역할 분담

| 이름 | 담당 |
| --- | --- |
| 안정후 | Hub / Route |
| 여정진 | Company / Product |
| 김준원 | Order |
| 이현수 | Shipment |
| 김지원 | User / Auth |
| 이호영 | AI / Slack |

---

## 📄 API 문서

Swagger 기반 API 문서 제공

```
<http://localhost>:{port}/swagger-ui/index.html
```

---

## 📌 설계 특징

- 도메인 기반 서비스 분리 (MSA)
- Gateway + Eureka 기반 서비스 관리
- 이벤트 기반 아키텍처 (RabbitMQ)
- Soft Delete 공통 적용
- 검색 / 정렬 / 페이지네이션 표준화
- 내부 API (`/internal`) 구조 분리

---

## 🚀 한 줄 소개

**주문부터 배송, 발송 관리까지 처리하는 MSA 기반 물류 시스템**
