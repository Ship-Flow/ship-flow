package com.shipflow.notificationservice.config;

import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@ConditionalOnProperty(name = "spring.datasource.url")
@EnableJpaRepositories(basePackages = "com.shipflow.notificationservice")
@EntityScan(basePackages = "com.shipflow.notificationservice")
public class JPAConfig {

	private static final UUID SYSTEM_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	@PersistenceContext
	private EntityManager em;

	@Bean
	@ConditionalOnMissingBean(JPAQueryFactory.class)
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(em);
	}

	@Bean
	public AuditorAware<UUID> auditorAware() {
		return () -> {
			try {
				ServletRequestAttributes attrs =
					(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
				// RabbitMQ 컨슈머 스레드 등 요청 컨텍스트가 없는 경우 → 시스템 UUID
				if (attrs == null)
					return Optional.of(SYSTEM_UUID);
				String userId = attrs.getRequest().getHeader("X-User-Id");
				if (userId == null || userId.isBlank())
					return Optional.of(SYSTEM_UUID);
				return Optional.of(UUID.fromString(userId));
			} catch (Exception e) {
				return Optional.of(SYSTEM_UUID);
			}
		};
	}
}