# 주문 생성까지의 Postman 시나리오

## 서비스별 포트 정보

| 서비스 | 로컬 접속 주소 | 비고 |
|---|---|---|
| **Gateway** | `http://localhost:8000` | **모든 API 진입점** |
| **Keycloak** | `http://localhost:9001` | Admin Console |
| **Eureka** | `http://localhost:8761` | 서비스 디스커버리 UI |
| **Zipkin** | `http://localhost:9411` | 분산 트레이싱 UI |
| **RabbitMQ Management** | `http://localhost:15672` | 메시지 큐 관리 UI |
| user-service | `http://localhost:8083` | 직접 접속 (테스트용) |
| order-service | `http://localhost:8082` | 직접 접속 (테스트용) |
| product-service | `http://localhost:8090` | 직접 접속 (테스트용) |
| company-service | `http://localhost:8084` | 직접 접속 (테스트용) |
| hub-service | `http://localhost:8085` | 직접 접속 (테스트용) |
| shipment-service | `http://localhost:8086` | 직접 접속 (테스트용) |
| notification-service | 포트 미노출 (내부망 전용) | - |

> Postman에서는 **Gateway(`http://localhost:8000`)** 를 통해 접속합니다.

---

## Postman Environment 설정

| Variable | Initial Value |
|---|---|
| `base_url` | `http://localhost:8000` |
| `token` | _(로그인 후 자동 저장)_ |
| `userId` | _(로그인 후 수동 입력)_ |
| `hubId` | _(허브 생성 후 자동 저장)_ |
| `vendorCompanyId` | _(업체 생성 후 자동 저장)_ |
| `receiverCompanyId` | _(업체 생성 후 자동 저장)_ |
| `productId` | _(상품 생성 후 자동 저장)_ |
| `orderId` | _(주문 생성 후 자동 저장)_ |

---

## 시나리오

### Step 1. 로그인

```
POST {{base_url}}/api/auth/login
Content-Type: application/json
```

**Request Body**
```json
{
  "username": "master",
  "password": "master1234"
}
```

**Tests 탭 (자동 저장)**
```javascript
pm.environment.set("token", pm.response.json().data.accessToken);
```

---

### Step 2. 허브 생성

```
POST {{base_url}}/api/hubs
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Request Body**
```json
{
  "name": "서울 허브",
  "address": "서울시 강남구 테헤란로 123",
  "latitude": 37.5013,
  "longitude": 127.0397,
  "managerId": "{{userId}}",
  "managerName": "허브관리자"
}
```

**Tests 탭 (자동 저장)**
```javascript
pm.environment.set("hubId", pm.response.json().data.id);
```

---

### Step 3-A. 공급업체 등록 (VENDOR)

```
POST {{base_url}}/api/companies
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Request Body**
```json
{
  "name": "테스트 공급업체",
  "type": "VENDOR",
  "hubId": "{{hubId}}",
  "address": "서울시 강남구 공급로 1",
  "managerId": "{{userId}}"
}
```

**Tests 탭 (자동 저장)**
```javascript
pm.environment.set("vendorCompanyId", pm.response.json().data.id);
```

---

### Step 3-B. 수령업체 등록 (RECEIVER)

```
POST {{base_url}}/api/companies
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Request Body**
```json
{
  "name": "테스트 수령업체",
  "type": "RECEIVER",
  "hubId": "{{hubId}}",
  "address": "서울시 마포구 수령로 2",
  "managerId": "{{userId}}"
}
```

**Tests 탭 (자동 저장)**
```javascript
pm.environment.set("receiverCompanyId", pm.response.json().data.id);
```

---

### Step 4. 상품 생성

```
POST {{base_url}}/api/companies/{{vendorCompanyId}}/products
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Request Body**
```json
{
  "name": "테스트 상품",
  "price": 10000,
  "stock": 100,
  "status": "AVAILABLE"
}
```

`status` 가능 값: `AVAILABLE`, `UNAVAILABLE`

**Tests 탭 (자동 저장)**
```javascript
pm.environment.set("productId", pm.response.json().data.id);
```

---

### Step 5. 주문 생성

```
POST {{base_url}}/api/orders
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Request Body**
```json
{
  "productId": "{{productId}}",
  "quantity": 3,
  "requestDeadline": "2026-04-30T18:00:00",
  "requestNote": "빠른 배송 부탁드립니다"
}
```

**Tests 탭 (자동 저장)**
```javascript
pm.environment.set("orderId", pm.response.json().data.id);
```

> 주문 생성 성공 시 RabbitMQ를 통해 비동기로 재고 감소 이벤트 발행

---

### Step 6. 주문 조회

```
GET {{base_url}}/api/orders/{{orderId}}
Authorization: Bearer {{token}}
```

또는 목록 조회:

```
GET {{base_url}}/api/orders?page=0&size=10
Authorization: Bearer {{token}}
```

---

### Step 7. 재고 감소 확인

```
GET {{base_url}}/api/companies/{{vendorCompanyId}}/products/{{productId}}
Authorization: Bearer {{token}}
```

`stock` 필드가 `97` (100 - 3)로 감소했는지 확인
> 재고 감소는 RabbitMQ 비동기 처리이므로 주문 직후 1~2초 후 확인

---

## 외부 도구 접속 정보

| 도구 | 주소 | 계정 |
|---|---|---|
| Keycloak Admin | `http://localhost:9001/admin` | `.env`의 `KEYCLOAK_ADMIN` / `KEYCLOAK_ADMIN_PASSWORD` |
| Eureka 대시보드 | `http://localhost:8761` | - |
| Zipkin UI | `http://localhost:9411` | - |
| RabbitMQ 관리 | `http://localhost:15672` | `.env`의 `RABBITMQ_USERNAME` / `RABBITMQ_PASSWORD` |
