package com.shipflow.notificationservice.infrastructure.client.ai;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.notificationservice.domain.ai.AiGenerator;
import com.shipflow.notificationservice.domain.ai.exception.AiErrorCode;
import com.shipflow.notificationservice.domain.ai.vo.AiResponseInfo;
import com.shipflow.notificationservice.infrastructure.client.ai.config.GeminiProperties;
import com.shipflow.notificationservice.infrastructure.client.ai.dto.GeminiRequest;
import com.shipflow.notificationservice.infrastructure.client.ai.dto.GeminiResponse;

import reactor.core.publisher.Mono;

@Component
public class GeminiApiClient implements AiGenerator {

	private static final String API_KEY_HEADER = "x-goog-api-key";
	private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);
	private static final Pattern DEADLINE_PATTERN =
		Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(?::\\d{2})?");

	private final WebClient webClient;
	private final GeminiProperties geminiProperties;

	public GeminiApiClient(
		@Qualifier("geminiWebClient") WebClient webClient,
		GeminiProperties geminiProperties
	) {
		this.webClient = webClient;
		this.geminiProperties = geminiProperties;
	}

	@Override
	public AiResponseInfo generate(String prompt) {

		validatePrompt(prompt);
		GeminiResponse response;

		try {
			response = webClient.post()
				.uri(geminiProperties.getUrl())
				.header(API_KEY_HEADER, geminiProperties.getApiKey())
				.header("Content-Type", "application/json")
				.bodyValue(new GeminiRequest(prompt))
				.retrieve()
				.onStatus(
					status -> status.is4xxClientError(),
					res -> Mono.error(new BusinessException(AiErrorCode.AI_GENERATE_FAILED))
				)
				.onStatus(
					status -> status.is5xxServerError(),
					res -> Mono.error(new BusinessException(AiErrorCode.AI_GENERATE_FAILED))
				)
				.bodyToMono(GeminiResponse.class)
				.timeout(REQUEST_TIMEOUT)
				.block();
		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw new BusinessException(AiErrorCode.AI_GENERATE_FAILED);
		}

		String text = extractText(response);
		LocalDateTime finalDeadlineAt = extractDeadline(text);

		return new AiResponseInfo(text, finalDeadlineAt);
	}

	private void validatePrompt(String prompt) {
		if (prompt == null || prompt.isBlank()) {
			throw new BusinessException(AiErrorCode.AI_PROMPT_REQUIRED);
		}
	}

	private String extractText(GeminiResponse response) {
		if (response == null
			|| response.getCandidates() == null
			|| response.getCandidates().isEmpty()
			|| response.getCandidates().get(0).getContent() == null
			|| response.getCandidates().get(0).getContent().getParts() == null
			|| response.getCandidates().get(0).getContent().getParts().isEmpty()
			|| response.getCandidates().get(0).getContent().getParts().get(0).getText() == null
			|| response.getCandidates().get(0).getContent().getParts().get(0).getText().isBlank()) {
			throw new BusinessException(AiErrorCode.AI_RESPONSE_EMPTY);
		}

		return response.getCandidates()
			.get(0)
			.getContent()
			.getParts()
			.get(0)
			.getText()
			.trim();
	}

	private LocalDateTime extractDeadline(String text) {
		Matcher matcher = DEADLINE_PATTERN.matcher(text);
		if (!matcher.find()) {
			throw new BusinessException(AiErrorCode.AI_RESPONSE_PARSE_FAILED);
		}

		try {
			return LocalDateTime.parse(matcher.group());
		} catch (DateTimeParseException e) {
			throw new BusinessException(AiErrorCode.AI_RESPONSE_PARSE_FAILED);
		}
	}
}