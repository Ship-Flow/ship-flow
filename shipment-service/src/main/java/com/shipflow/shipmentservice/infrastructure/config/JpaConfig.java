package com.shipflow.shipmentservice.infrastructure.config;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.EntityManager;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.querydsl.jpa.impl.JPAQueryFactory;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

	@Bean
	@ConditionalOnMissingBean(JPAQueryFactory.class)
	public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
		return new JPAQueryFactory(entityManager);
	}

	@Bean
	public AuditorAware<UUID> auditorAware() {
		return () -> {
			/*
			 * TODO:
			 * Spring Security 연동 후
			 * SecurityContext에서 로그인 사용자 UUID를 추출하도록 변경
			 */
			return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		};
	}
}
