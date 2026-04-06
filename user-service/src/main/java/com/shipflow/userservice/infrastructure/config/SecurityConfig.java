package com.shipflow.userservice.infrastructure.config;

import static jakarta.ws.rs.HttpMethod.*;
import static org.springframework.security.config.Customizer.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Value("${security.oauth2.resourceserver.jwt.jwk-set-uri}")
	private String jwkSetUri;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/actuator/**").permitAll()
				.requestMatchers("/api/auth/login").permitAll()
				.requestMatchers(POST, "/api/auth/signup-requests").permitAll()
				.requestMatchers("/internal/**").authenticated()
				.anyRequest().authenticated()
			)
			.oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));

		return http.build();
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		// jwk-set-uri 방식: 서명만 검증, issuer claim 체크 없음
		// → Docker 환경에서 localhost(Postman) vs keycloak(내부) 불일치 문제 해결
		return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
	}
}