# MSA E2E 테스트 환경 구축 중 발생한 버그 수정 요약

## Context

여러 팀원이 독립적으로 각 서비스를 개발한 MSA 환경에서 E2E 테스트를 처음 실행하면서 발생한 통합 문제들을 수정한 내역입니다. 다음번에 같은 실수를 반복하지 않도록 원인과 교훈을 정리합니다.

---

## 수정 내역 및 교훈

### 1. Eureka 서비스 이름 불일치 (503 Service Unavailable)

**수정 파일:**
- `company-service/src/main/resources/application.yaml`
- `order-service/src/main/resources/application.yaml`
- `product-service/src/main/resources/application.yaml`

**문제:** `spring.application.name: company-service` (하이픈 포함) → Eureka에 `COMPANY-SERVICE`로 등록. 반면 `@FeignClient(name = "companyservice")`는 `COMPANYSERVICE`를 찾음 → DNS UnknownHostException.

**수정:** `application.name`을 FeignClient name과 일치시킴 (`companyservice`, `orderservice`, `productservice`).

**교훈:** `@FeignClient(name = "xxx")`의 값과 대상 서비스의 `spring.application.name`은 반드시 동일해야 함. 하이픈/카멜케이스 혼용 금지.

---

### 2. FeignClient name 불일치

**수정 파일:**
- `company-service/.../UserFeignClient.java` → `"user-service"` → `"userservice"`
- `company-service/.../ProductFeignClient.java` → `"product-service"` → `"productservice"`
- `product-service/.../VendorFeignClient.java` → `"company-service"` → `"companyservice"`

**교훈:** FeignClient name은 상대 서비스의 `spring.application.name`과 완전히 동일해야 함. 팀 내에서 서비스 이름 컨벤션을 문서화할 것.

---

### 3. FeignClient PATCH 메서드 실패 (JDK HttpURLConnection PATCH 미지원)

**수정 파일:**
- `company-service/build.gradle` → `feign-okhttp` 의존성 추가
- `company-service/src/main/resources/application.yaml` → `openfeign.okhttp.enabled: true`
- `company-service/.../UserFeignClient.java` → `updateCompanyManager()`에 `@RequestBody` 추가

**문제:** JDK 기본 HttpURLConnection은 PATCH 메서드를 지원하지 않아 `Invalid HTTP method: PATCH` 에러 발생.

**교훈:** Feign에서 PATCH를 사용하려면 반드시 OkHttp 설정이 필요. `@RequestBody`도 빠지지 않도록 주의.

---

### 4. UserInfoResponse DTO 불일치 (DecodeException)

**수정 파일:**
- `company-service/.../UserInfoResponse.java`
- `company-service/.../CompanyService.java`

**문제:** user-service의 `/internal/users/{id}`가 `ApiResponse<{id, name, ...}>` 래퍼 구조로 응답하는데, company-service의 `UserInfoResponse`는 `{id, name, companyId, hubId}` flat 구조를 기대 → 역직렬화 실패.

**수정:** `UserInfoResponse`를 `{success, data{id,name,...}, error}` 래퍼 구조로 변경, `.data().name()` 방식으로 접근.

**교훈:** 서비스 간 Feign 연동 시 실제 응답 JSON 구조를 확인하고 DTO를 맞춰야 함. `@NonNull`이 붙은 필드가 null이면 NPE 발생.

---

### 5. ApiResponse Jackson 역직렬화 불가 (DecodeException)

**수정 파일:**
- `common/.../ApiResponse.java` → `@JsonCreator` static factory 추가

**문제:** `ApiResponse`의 생성자가 `@AllArgsConstructor(access = AccessLevel.PRIVATE)` → private. Jackson이 인스턴스를 만들 수 없어 `Type definition error` 발생. Feign 응답 역직렬화 시 전부 실패.

**수정:**
```java
@JsonCreator
public static <T> ApiResponse<T> of(
    @JsonProperty("success") boolean success,
    @JsonProperty("data") T data,
    @JsonProperty("error") ErrorResponse error) {
    return new ApiResponse<>(success, data, error);
}
```

**교훈:** Feign 응답 타입으로 사용되는 클래스는 반드시 Jackson이 역직렬화 가능해야 함. `private` 생성자만 있는 클래스는 `@JsonCreator`가 필수.

---

### 6. Feign 응답 DTO 필드명 불일치 (@JsonProperty 매핑)

**수정 파일:**
- `product-service/.../VendorInfoResponse.java`
- `order-service/.../UserInfo.java`
- `order-service/.../ReceiverCompanyInfo.java`

**문제:** 제공 서비스의 응답 JSON 필드명과 소비 서비스의 DTO 필드명이 다름. 예: company-service는 `receiverCompanyId`를 반환하는데 order-service는 `companyId`를 기대.

**수정:** 소비 서비스의 DTO에 `@JsonProperty("실제_JSON_필드명")`으로 매핑.

**교훈:** MSA에서 서비스마다 독립적으로 개발하면 필드명이 달라질 수 있음. 소비자 쪽에서 `@JsonProperty`로 매핑하면 제공자를 수정하지 않아도 됨 (Consumer-Driven Contract 원칙).

---

### 7. Spring Security 기본 설정이 모든 요청을 차단

**수정 파일:**
- `company-service/.../SecurityConfig.java` (신규 생성)
- `product-service/build.gradle` → `spring-boot-starter-security`, `oauth2-resource-server` 의존성 제거

**문제:** `spring-boot-starter-security` 의존성만 있고 `SecurityConfig`가 없으면 Spring Boot가 모든 요청에 HTTP Basic Auth를 요구 → 401.

**교훈:** MSA에서 인증은 Gateway에서 처리하고 내부 서비스는 헤더(`X-User-Id`)를 신뢰하는 패턴 사용 시, 각 서비스에서 Security 의존성을 제거하거나 `permitAll()` SecurityConfig가 필요. 불필요한 Security 의존성은 애초에 추가하지 말 것.

---

### 8. `/internal/**` 경로에서 UserContextInterceptor가 X-User-Id 요구

**수정 파일:**
- `company-service/.../WebConfig.java` → `.excludePathPatterns("/internal/**")` 추가

**문제:** 서비스 간 Feign 호출은 `X-User-Id` 헤더 없이 호출하는데, 인터셉터가 해당 헤더를 요구해서 401 반환.

**교훈:** `/internal/**` 경로는 서비스 간 내부 통신용이므로 UserContext 인터셉터에서 반드시 제외해야 함.

---

### 9. Order 생성 시 updated_at, updated_by NOT NULL 위반

**수정 파일:**
- `order-service/.../Order.java` → `create()` 팩토리에 `updatedAt`, `updatedBy` 세팅 추가

**문제:** `Order.create()` 팩토리 메서드에서 `createdAt`/`createdBy`만 세팅하고 `updatedAt`/`updatedBy`는 누락. DB 컬럼이 NOT NULL이라 INSERT 실패.

**교훈:** JPA Auditing 없이 타임스탬프를 수동 관리할 때는 생성/수정 필드를 모두 함께 세팅해야 함. `@PrePersist` 훅을 사용하면 이런 실수를 방지할 수 있음.

---

### 10. Gateway issuer-uri 불일치 (401)

**수정 파일:**
- `gateway-server/src/main/resources/application.yaml` → `issuer-uri` → `jwk-set-uri`로 변경

**문제:** Keycloak 토큰의 `iss` claim은 `http://localhost:9001/realms/shipflow`인데, Docker 내부에서 `http://keycloak:8080`으로 검증 시도 → issuer 불일치로 JWT 검증 실패.

**수정:** issuer 검증 없이 서명만 검증하는 `jwk-set-uri` 방식으로 변경.

**교훈:** Docker 환경에서는 외부(localhost)와 내부(컨테이너명) URL이 다르므로 issuer 검증 시 불일치가 발생. `jwk-set-uri`로 서명만 검증하는 방식이 안전.

---

### 11. Gateway Keycloak 역할 매핑 누락 (403)

**수정 파일:**
- `gateway-server/.../SecurityConfig.java` → `keycloakJwtConverter()` 추가

**문제:** Keycloak JWT의 역할이 `realm_access.roles` 안에 있는데, Spring Security는 기본적으로 이 구조를 인식하지 못함 → 권한 없음으로 처리.

**수정:** `ReactiveJwtAuthenticationConverter`로 `realm_access.roles` → `ROLE_xxx` 형태로 변환.

**교훈:** Keycloak을 사용할 때는 반드시 realm_access.roles 매핑 커스터마이징이 필요.

---

### 12. docker-compose 환경 변수 및 포트 설정

**수정 파일:**
- `docker-compose.yml`

**변경 내용:**
- `x-service-env`에 `RABBITMQ_HOST`, `ZIPKIN_BASE_URL`, `KEYCLOAK_SERVER_URL` 환경변수 추가
- 각 서비스 포트 고정 (랜덤 → 고정): company `8084`, hub `8085`, order `8082`, product `8090`, user `8083`
- Zipkin 서비스 추가 (`openzipkin/zipkin`, port 9411)
- order/product에 `zipkin: condition: service_healthy` depends_on 추가

**교훈:** Postman 테스트를 위해서는 포트를 고정해야 함. 환경변수는 `x-service-env` 앵커로 공통 관리할 것.

---

## 핵심 체크리스트 (새 서비스 연동 시)

- [ ] `spring.application.name`과 `@FeignClient(name)`이 일치하는가?
- [ ] Feign 응답 DTO가 실제 JSON 구조와 일치하는가? (래퍼 여부, 필드명)
- [ ] `ApiResponse`처럼 private 생성자 클래스를 Feign 응답 타입으로 쓸 경우 `@JsonCreator`가 있는가?
- [ ] PATCH 메서드 사용 시 OkHttp 설정이 되어 있는가?
- [ ] `/internal/**` 경로가 UserContextInterceptor에서 제외되어 있는가?
- [ ] 불필요한 `spring-boot-starter-security` 의존성이 없는가?
- [ ] 도메인 모델 생성 팩토리에서 NOT NULL 컬럼이 모두 세팅되는가?
- [ ] Docker 환경에서 JWT 검증은 `jwk-set-uri` 방식을 사용하는가?
