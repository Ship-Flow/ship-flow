package com.shipflow.productservice.infrastructure.config;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import com.shipflow.productservice.infrastructure.web.UserContext;

@Configuration
public class JpaAuditConfig {
	@Bean
	public AuditorAware<UUID> auditorAware() {
		return () -> {
			UUID userId=UserContext.getUserId();

			if(userId==null)
				return Optional.empty();

			try {
				return Optional.of(userId);
			} catch (IllegalArgumentException e) {
				return Optional.empty();
			}
		};
	}
}
