package com.shipflow.notificationservice.application;

import java.util.Collections;
import java.util.List;
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
import com.shipflow.notificationservice.infrastructure.client.order.OrderInternalClient;
import com.shipflow.notificationservice.infrastructure.client.order.OrderReadModelResponse;
import com.shipflow.notificationservice.infrastructure.messaging.dto.ShipmentCreatedEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationOrchestratorService {

	private static final String DEFAULT_WORKING_HOURS = "09:00 ~ 18:00";
	private static final String UNKNOWN = "확인 필요";
	private static final UUID SYSTEM_USER_ID = new UUID(0L, 0L);

	private final AiAppService aiAppService;
	private final SlackAppService slackAppService;
	private final AiLogRepository aiLogRepository;
	private final OrderInternalClient orderInternalClient;

	public void handleShipmentCreated(ShipmentCreatedEvent event) {
		//1. 주문 정보 조회
		OrderReadModelResponse order = getOrderReadModel(event.getOrderId());
		String slackId = resolveSlackId(event);

		// 2. AI 호출 + AiLog 저장 → AiAppService
		GenerateDeadlineCommand command = toGenerateDeadlineCommand(event, order, slackId);
		AiLogResult aiResult = aiAppService.generateAiLog(command);
		// 3. 슬랙 메시지 생성
		String slackMessage = createDeadlineSlackMessage(command, aiResult, order);
		// 4. 슬랙 발송 + SlackMessage 저장 → SlackAppService 내부
		try {
			slackAppService.sendSlackMessage(
				new SendSlackMessageCommand(
					SYSTEM_USER_ID,
					"MASTER",
					slackId,
					event.getShipmentId(),
					aiResult.aiId(),
					slackMessage,
					SlackMessageType.DEADLINE_ALERT
				)
			);
			// 5. 슬랙 발송 성공 → AiLog 상태 업데이트 (별도 트랜잭션)
			markSlackSendSuccess(aiResult.aiId());
		} catch (Exception e) {
			// 6. 슬랙 발송 실패 → AiLog 상태 업데이트 (별도 트랜잭션)
			markSlackSendFail(aiResult.aiId());
			throw e;
		}
	}

	private GenerateDeadlineCommand toGenerateDeadlineCommand(
		ShipmentCreatedEvent event,
		OrderReadModelResponse order,
		String slackId
	) {
		return new GenerateDeadlineCommand(
			event.getOrderId(),
			SYSTEM_USER_ID,
			event.getShipmentId(),
			null,
			slackId,

			null,
			null,

			event.getProductId(),
			extractProductText(order),
			event.getQuantity(),

			event.getDepartureHubId(),
			order != null ? order.departureHubName() : UNKNOWN,
			event.getArrivalHubId(),
			order != null ? order.arrivalHubName() : UNKNOWN,
			extractRouteTexts(event),

			extractRequestNote(event),
			event.getRequestDeadline(),
			DEFAULT_WORKING_HOURS,

			AiRequestType.DEADLINE,
			null
		);
	}

	private OrderReadModelResponse getOrderReadModel(UUID orderId) {
		if (orderId == null)
			return null;
		try {
			return orderInternalClient.getOrderReadModel(orderId);
		} catch (Exception e) {
			return null;
		}
	}

	private String resolveSlackId(ShipmentCreatedEvent event) {
		String slackId = event.getShipmentManagerSlackId();
		return (slackId == null || slackId.isBlank()) ? UNKNOWN : slackId;
	}

	private String extractProductText(OrderReadModelResponse order) {
		return (order == null || order.productName() == null || order.productName().isBlank())
			? UNKNOWN
			: order.productName();
	}

	private List<String> extractRouteTexts(ShipmentCreatedEvent event) {
		if (event.getRoutes() == null || event.getRoutes().isEmpty()) {
			return Collections.emptyList();
		}

		return event.getRoutes().stream()
			.map(route -> {
				if (route.getSequence() == null) {
					return "경유";
				}
				return route.getSequence() + "번 경유";
			})
			.toList();
	}

	private String extractRequestNote(ShipmentCreatedEvent event) {
		return (event.getRequestNote() == null || event.getRequestNote().isBlank())
			? "없음"
			: event.getRequestNote();
	}

	private String createDeadlineSlackMessage(
		GenerateDeadlineCommand command,
		AiLogResult aiResult,
		OrderReadModelResponse order
	) {
		String routeText = (command.route() == null || command.route().isEmpty())
			? "없음"
			: String.join("\n", command.route());

		String requestNote = (command.requestNote() == null || command.requestNote().isBlank())
			? "없음"
			: command.requestNote();

		String ordererName = (order == null || order.ordererName() == null)
			? UNKNOWN
			: order.ordererName();

		String orderTime = (order == null || order.createdAt() == null)
			? UNKNOWN
			: order.createdAt().toString();

		return """
			🚚 배송 요청 알림
			
			주문 번호: %s
			주문자 정보: %s
			주문 시간: %s
			배송 번호: %s
			상품 정보: %s / 수량: %d개
			요청 사항: %s
			납기 기한: %s
			
			발송지: %s
			경유 경로:
			%s
			도착지: %s
			
			⏰ AI 계산 최종 발송 시한: %s
			
			※ 해당 시간 이전에 발송을 완료해주세요.
			""".formatted(
			command.orderId(),
			ordererName,
			orderTime,
			command.relatedShipmentId(),
			command.product(),
			command.quantity(),          // 수량
			requestNote,
			command.deadline(),          // 납기 기한
			command.fromHub(),
			routeText,
			command.toHub(),
			aiResult.finalDeadlineAt()
		);
	}

	@Transactional
	public void markSlackSendSuccess(UUID aiLogId) {
		AiLog aiLog = aiLogRepository.findByIdAndDeletedAtIsNull(aiLogId)
			.orElseThrow(() -> new BusinessException(AiErrorCode.AI_LOG_NOT_FOUND));
		aiLog.markSendSuccess();
	}

	@Transactional
	public void markSlackSendFail(UUID aiLogId) {
		AiLog aiLog = aiLogRepository.findByIdAndDeletedAtIsNull(aiLogId)
			.orElseThrow(() -> new BusinessException(AiErrorCode.AI_LOG_NOT_FOUND));
		aiLog.markSendFail();
	}
}