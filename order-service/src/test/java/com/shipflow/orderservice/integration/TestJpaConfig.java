package com.shipflow.orderservice.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@TestConfiguration
@EnableJpaAuditing(auditorAwareRef = "testAuditorAware")
public class TestJpaConfig {

    @Bean
    public AuditorAware<UUID> testAuditorAware() {
        return () -> Optional.of(UUID.fromString("22222222-2222-2222-2222-222222222222"));
    }
}
