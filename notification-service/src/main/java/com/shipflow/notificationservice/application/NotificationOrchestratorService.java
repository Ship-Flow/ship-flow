package com.shipflow.notificationservice.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shipflow.common.exception.BusinessException;
import com.shipflow.notificationservice.application.ai.AiAppService;
import com.shipflow.notificationservice.application.ai.dto.command.GenerateDeadlineCommand;
import com.shipflow.notificationservice.application.ai.dto.result.AiLogResult;
import com.shipflow.notificationservice.application.slack.SlackAppService;
import com.shipflow.notificationservice.application.slack.dto.command.SendSlackMessageCommand;
import com.shipflow.notificationservice.domain.ai.AiLog;
import com.shipflow.notificationservice.domain.ai.exception.AiErrorCode;
import com.shipflow.notificationservice.domain.ai.repository.AiLogRepository;
import com.shipflow.notificationservice.domain.ai.type.AiRequestType;
import com.shipflow.notificationservice.domain.slack.type.SlackMessageType;
import com.shipflow.notificationservice.infrastructure.messaging.dto.ShipmentCreatedEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class NotificationOrchestratorService {

	private static final String DEFAULT_WORKING_HOURS = "09:00 ~ 18:00";
	private static final String UNKNOWN = "확인 필요";

	private final AiAppService aiAppService;
	private final SlackAppService slackAppService;
	private final AiLogRepository aiLogRepository;

	@Transactional
	public void handleShipmentCreated(ShipmentCreatedEvent event) {
		GenerateDeadlineCommand command = toGenerateDeadlineCommand(event);

		AiLogResult aiResult = aiAppService.generateAiLog(command);

		String slackMessage = createDeadlineSlackMessage(command, aiResult);

		try {
			slackAppService.sendSlackMessage(
				new SendSlackMessageCommand(
					event.getReceiverSlackId(),
					command.relatedShipmentId(),
					aiResult.aiId(),
					slackMessage,
					SlackMessageType.DEADLINE_ALERT
				)
			);

			markSlackSendSuccess(aiResult.aiId());

		} catch (Exception e) {
			markSlackSendFail(aiResult.aiId());
			throw e;
		}
	}

	private GenerateDeadlineCommand toGenerateDeadlineCommand(ShipmentCreatedEvent event) {
		return new GenerateDeadlineCommand(
			event.getShipmentId(),              // relatedShipmentId
			null,                               // shipmentManagerId
			extractFromHub(event),              // fromHub
			extractToHub(event),                // toHub
			java.util.Collections.emptyList(),  // route
			extractProductText(event),          // product
			extractRequestNote(event),          // requestNote
			event.getRequestDeadline(),         // deadline
			DEFAULT_WORKING_HOURS,              // workingHours
			AiRequestType.DEADLINE,             // requestType
			null                                // workDate
		);
	}

	private String extractFromHub(ShipmentCreatedEvent event) {
		return event.getDepartureHubId() == null
			? UNKNOWN
			: "허브ID: " + event.getDepartureHubId();
	}

	private String extractToHub(ShipmentCreatedEvent event) {
		return event.getArrivalHubId() == null
			? UNKNOWN
			: "허브ID: " + event.getArrivalHubId();
	}

	private String extractProductText(ShipmentCreatedEvent event) {
		String productId = event.getProductId() == null ? UNKNOWN : event.getProductId().toString();
		String quantity = event.getQuantity() == null ? "수량 미확인" : event.getQuantity() + "개";

		return "상품ID: " + productId + " / 수량: " + quantity;
	}

	private String extractRequestNote(ShipmentCreatedEvent event) {
		return (event.getRequestNote() == null || event.getRequestNote().isBlank())
			? "없음"
			: event.getRequestNote();
	}

	private String createDeadlineSlackMessage(GenerateDeadlineCommand command, AiLogResult aiResult) {
		String routeText = (command.route() == null || command.route().isEmpty())
			? "없음"
			: String.join(" → ", command.route());

		String requestNote = (command.requestNote() == null || command.requestNote().isBlank())
			? "없음"
			: command.requestNote();

		return """
			🚚 배송 요청 알림
			
			상품 정보: %s
			요청 사항: %s
			
			발송지: %s
			경유지: %s
			도착지: %s
			
			⏰ AI 계산 최종 발송 시한: %s
			
			※ 해당 시간 이전에 발송을 완료해주세요.
			""".formatted(
			command.product(),
			requestNote,
			command.fromHub(),
			routeText,
			command.toHub(),
			aiResult.finalDeadlineAt()
		);
	}

	private void markSlackSendSuccess(UUID aiLogId) {
		AiLog aiLog = aiLogRepository.findByIdAndDeletedAtIsNull(aiLogId)
			.orElseThrow(() -> new BusinessException(AiErrorCode.AI_LOG_NOT_FOUND));

		aiLog.markSendSuccess();
	}

	private void markSlackSendFail(UUID aiLogId) {
		AiLog aiLog = aiLogRepository.findByIdAndDeletedAtIsNull(aiLogId)
			.orElseThrow(() -> new BusinessException(AiErrorCode.AI_LOG_NOT_FOUND));

		aiLog.markSendFail();
	}
}