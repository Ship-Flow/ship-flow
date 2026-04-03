package com.shipflow.hubservice.config;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

	@Bean
	public AuditorAware<UUID> auditorAware() {
		return () -> {
			try {
				ServletRequestAttributes attrs =
					(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
				if (attrs == null) return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
				String userId = attrs.getRequest().getHeader("X-User-Id");
				if (userId == null || userId.isBlank()) return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
				return Optional.of(UUID.fromString(userId));
			} catch (Exception e) {
				return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
			}
		};
	}
}
