package com.shipflow.userservice.infrastructure.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
			.requestFactory(() -> {
				SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
				factory.setConnectTimeout((int) Duration.ofSeconds(3).toMillis());
				factory.setReadTimeout((int) Duration.ofSeconds(5).toMillis());
				return factory;
			})
			.build();
	}
}
