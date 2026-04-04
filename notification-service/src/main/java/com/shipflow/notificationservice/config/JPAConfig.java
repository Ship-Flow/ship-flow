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

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@ConditionalOnProperty(name = "spring.datasource.url")
@EnableJpaRepositories(basePackages = "com.shipflow.notificationservice")
@EntityScan(basePackages = "com.shipflow.notificationservice")
public class JPAConfig {

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
			/*
			 * TODO: Spring Security 연동 시 아래 방식으로 변경
			 * - SecurityContext에서 로그인 사용자 UUID 추출하여 반환
			 */
			return Optional.of(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		};
	}
}