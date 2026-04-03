package com.shipflow.notificationservice.infrastructure.client.slack.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "slack")
@Validated
public class SlackProperties {

	@NotBlank
	private String botToken;

	public String getBotToken() {
		return botToken;
	}

	public void setBotToken(String botToken) {
		this.botToken = botToken;
	}
}
