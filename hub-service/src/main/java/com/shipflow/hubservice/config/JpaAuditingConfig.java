package com.shipflow.hubservice.config;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

	@Bean
	public AuditorAware<UUID> auditorAware() {
		// Phase 4에서 X-User-Id 헤더 기반으로 교체 예정
		return () -> Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
	}
}
