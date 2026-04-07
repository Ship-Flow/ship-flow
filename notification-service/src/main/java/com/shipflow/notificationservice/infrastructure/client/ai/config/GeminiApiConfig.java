package com.shipflow.notificationservice.infrastructure.client.ai.config;

import java.time.Duration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties(GeminiProperties.class)
public class GeminiApiConfig {

	@Bean(name = "geminiWebClient")
	public WebClient geminiWebClient() {
		HttpClient httpClient = HttpClient.create()
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
			.responseTimeout(Duration.ofSeconds(30));

		return WebClient.builder()
			.clientConnector(new ReactorClientHttpConnector(httpClient))
			.build();
	}
}