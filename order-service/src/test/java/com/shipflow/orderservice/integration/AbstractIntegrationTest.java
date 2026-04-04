package com.shipflow.orderservice.integration;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
public abstract class AbstractIntegrationTest {

    // 싱글턴 컨테이너 패턴: 테스트 클래스 간 컨테이너 재시작 방지
    // @Container/@Testcontainers 미사용 → JVM 종료 시까지 컨테이너 유지
    static final PostgreSQLContainer<?> postgres;
    static final RabbitMQContainer rabbitmq;

    static {
        postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                .withInitScript("test-schema.sql");
        rabbitmq = new RabbitMQContainer("rabbitmq:3.13-management-alpine");
        postgres.start();
        rabbitmq.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                () -> postgres.getJdbcUrl() + "?currentSchema=orders");
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "orders");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");

        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);

        registry.add("spring.cloud.discovery.enabled", () -> false);
        registry.add("eureka.client.enabled", () -> false);
    }
}
