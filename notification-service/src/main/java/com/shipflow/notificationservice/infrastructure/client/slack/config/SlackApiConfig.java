package com.shipflow.notificationservice.infrastructure.client.slack.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.slack.api.Slack;

@Configuration
@EnableConfigurationProperties(SlackProperties.class)
public class SlackApiConfig {

	@Bean
	public Slack slack() {
		return Slack.getInstance();
	}
}