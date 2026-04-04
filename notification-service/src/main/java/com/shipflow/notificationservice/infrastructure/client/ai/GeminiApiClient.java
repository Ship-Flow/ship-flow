package com.shipflow.notificationservice.infrastructure.client.ai;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.notificationservice.domain.ai.AiGenerator;
import com.shipflow.notificationservice.domain.ai.exception.AiErrorCode;
import com.shipflow.notificationservice.domain.ai.vo.AiRequestInfo;
import com.shipflow.notificationservice.domain.ai.vo.AiResponseInfo;
import com.shipflow.notificationservice.infrastructure.client.ai.config.GeminiProperties;
import com.shipflow.notificationservice.infrastructure.client.ai.dto.GeminiRequest;
import com.shipflow.notificationservice.infrastructure.client.ai.dto.GeminiResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class GeminiApiClient implements AiGenerator {

	private static final String API_KEY_HEADER = "x-goog-api-key";
	private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);
	private static final Pattern DEADLINE_PATTERN =
		Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(?::\\d{2})?");
	private static final String WORKING_HOURS = "09:00 ~ 18:00";

	private final WebClient webClient;
	private final GeminiProperties geminiProperties;

	@Override
	public AiResponseInfo generate(AiRequestInfo aiRequestInfo) {
		validateRequestInfo(aiRequestInfo);
		GeminiResponse response;
		String prompt = createPrompt(aiRequestInfo);

		try {
			response = webClient.post()
				.uri(geminiProperties.getUrl())
				.header(API_KEY_HEADER, geminiProperties.getApiKey())
				.bodyValue(new GeminiRequest(prompt))
				.retrieve()

				// 4xx 에러 처리 (잘못된 요청)
				.onStatus(
					status -> status.is4xxClientError(),
					res -> Mono.error(new BusinessException(AiErrorCode.AI_GENERATE_FAILED))
				)

				// 5xx 에러 처리 (서버 오류)
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

	//프롬포트
	private String createPrompt(AiRequestInfo info) {
		String routeText = (info.route() == null || info.route().isEmpty())
			? "없음"
			: String.join(", ", info.route());
		String requestNote = (info.requestNote() == null || info.requestNote().isBlank())
			? "없음"
			: info.requestNote();

		return """
			다음 물류 정보를 바탕으로 최종 발송 시한을 계산해라.
			
			발송지: %s
			경유지: %s
			도착지: %s
			상품: %s
			요청사항: %s
			납기: %s
			근무시간: %s
			
			반드시 ISO-8601 형식의 발송 시한만 포함해서 응답해라.
			예시: 2026-04-04T09:00:00
			""".formatted(
			info.fromHub(),
			routeText,
			info.toHub(),
			info.product(),
			info.requestNote(),
			info.deadline(),
			WORKING_HOURS
		);
	}

	//유효성 검증
	private void validateRequestInfo(AiRequestInfo aiRequestInfo) {
		if (aiRequestInfo == null) {
			throw new BusinessException(AiErrorCode.AI_EVENT_NOT_FOUND);
		}
		if (aiRequestInfo.requestType() == null) {
			throw new BusinessException(AiErrorCode.AI_REQUEST_TYPE_REQUIRED);
		}
		if (aiRequestInfo.fromHub() == null || aiRequestInfo.fromHub().isBlank()) {
			throw new BusinessException(AiErrorCode.AI_FROM_HUB_REQUIRED);
		}

		if (aiRequestInfo.toHub() == null || aiRequestInfo.toHub().isBlank()) {
			throw new BusinessException(AiErrorCode.AI_TO_HUB_REQUIRED);
		}

		if (aiRequestInfo.product() == null || aiRequestInfo.product().isBlank()) {
			throw new BusinessException(AiErrorCode.AI_PRODUCT_REQUIRED);
		}

		if (aiRequestInfo.deadline() == null) {
			throw new BusinessException(AiErrorCode.AI_DEADLINE_REQUIRED);
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

}//끝