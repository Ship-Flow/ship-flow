package com.shipflow.notificationservice.infrastructure.client.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "gemini")
public class GeminiProperties {

	@NotNull
	private String apiKey;
	@NotNull
	private String url;

}

