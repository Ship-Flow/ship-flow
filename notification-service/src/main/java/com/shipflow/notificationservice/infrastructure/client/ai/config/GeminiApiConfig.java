package com.shipflow.notificationservice.infrastructure.client.ai.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(GeminiProperties.class)
public class GeminiApiConfig {
	@Bean(name = "geminiWebClient")
	public WebClient geminiWebClient() {
		return WebClient.builder().build();
	}
}
