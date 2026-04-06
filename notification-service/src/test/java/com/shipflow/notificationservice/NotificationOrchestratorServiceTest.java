package com.shipflow.notificationservice;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shipflow.notificationservice.application.NotificationOrchestratorService;
import com.shipflow.notificationservice.application.ai.AiAppService;
import com.shipflow.notificationservice.application.ai.dto.result.AiLogResult;
import com.shipflow.notificationservice.application.slack.SlackAppService;
import com.shipflow.notificationservice.domain.ai.AiLog;
import com.shipflow.notificationservice.domain.ai.repository.AiLogRepository;
import com.shipflow.notificationservice.domain.ai.type.AiRequestType;
import com.shipflow.notificationservice.infrastructure.client.order.OrderInternalClient;
import com.shipflow.notificationservice.infrastructure.client.order.OrderReadModelResponse;
import com.shipflow.notificationservice.infrastructure.messaging.dto.ShipmentCreatedEvent;

@ExtendWith(MockitoExtension.class)
class NotificationOrchestratorServiceTest {

	@Mock
	private AiAppService aiAppService;
	@Mock
	private SlackAppService slackAppService;
	@Mock
	private AiLogRepository aiLogRepository;
	@Mock
	private OrderInternalClient orderInternalClient;

	@InjectMocks
	private NotificationOrchestratorService notificationOrchestratorService;

	@Test
	@DisplayName("정상적인 이벤트 수신 시 슬랙 알림 발송 성공")
	void handleShipmentCreated_success() {
		// given
		ShipmentCreatedEvent event = createEvent("U123SLACK");

		OrderReadModelResponse order = new OrderReadModelResponse(
			event.getOrderId(),
			UUID.randomUUID().toString(),
			"마른 오징어",
			"경기 북부 센터",
			"부산광역시 센터"
		);

		AiLogResult aiResult = mock(AiLogResult.class);
		when(aiResult.aiId()).thenReturn(UUID.randomUUID());
		when(aiResult.finalDeadlineAt()).thenReturn(LocalDateTime.now());

		AiLog aiLog = new AiLog(UUID.randomUUID(), UUID.randomUUID(), "prompt", AiRequestType.DEADLINE);
		aiLog.markSuccess("response", LocalDateTime.now());

		when(orderInternalClient.getOrderReadModel(event.getOrderId())).thenReturn(order);
		when(aiAppService.generateAiLog(any())).thenReturn(aiResult);
		when(aiLogRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(aiLog));

		// when & then
		assertThatNoException()
			.isThrownBy(() -> notificationOrchestratorService.handleShipmentCreated(event));

		verify(orderInternalClient).getOrderReadModel(event.getOrderId());
		verify(aiAppService).generateAiLog(any());
		verify(slackAppService).sendSlackMessage(any());
	}

	@Test
	@DisplayName("order read-model 조회 실패 시 UNKNOWN으로 대체하여 진행")
	void handleShipmentCreated_orderFail_continueWithUnknown() {
		// given
		ShipmentCreatedEvent event = createEvent("U123SLACK");

		AiLogResult aiResult = mock(AiLogResult.class);
		when(aiResult.aiId()).thenReturn(UUID.randomUUID());
		when(aiResult.finalDeadlineAt()).thenReturn(LocalDateTime.now());

		AiLog aiLog = new AiLog(UUID.randomUUID(), UUID.randomUUID(), "prompt", AiRequestType.DEADLINE);
		aiLog.markSuccess("response", LocalDateTime.now());

		when(orderInternalClient.getOrderReadModel(any())).thenThrow(new RuntimeException("조회 실패"));
		when(aiAppService.generateAiLog(any())).thenReturn(aiResult);
		when(aiLogRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(aiLog));

		// when & then
		assertThatNoException()
			.isThrownBy(() -> notificationOrchestratorService.handleShipmentCreated(event));
	}

	@Test
	@DisplayName("slackId가 null이면 UNKNOWN으로 대체")
	void handleShipmentCreated_nullSlackId() {
		// given
		ShipmentCreatedEvent event = createEvent(null); // slackId null

		OrderReadModelResponse order = new OrderReadModelResponse(
			event.getOrderId(),
			UUID.randomUUID().toString(),
			"마른 오징어",
			"경기 북부 센터",
			"부산광역시 센터"
		);

		AiLogResult aiResult = mock(AiLogResult.class);
		when(aiResult.aiId()).thenReturn(UUID.randomUUID());
		when(aiResult.finalDeadlineAt()).thenReturn(LocalDateTime.now());

		AiLog aiLog = new AiLog(UUID.randomUUID(), UUID.randomUUID(), "prompt", AiRequestType.DEADLINE);
		aiLog.markSuccess("response", LocalDateTime.now());

		when(orderInternalClient.getOrderReadModel(any())).thenReturn(order);
		when(aiAppService.generateAiLog(any())).thenReturn(aiResult);
		when(aiLogRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(aiLog));

		// when & then - UNKNOWN으로 대체되어 진행됨
		assertThatNoException()
			.isThrownBy(() -> notificationOrchestratorService.handleShipmentCreated(event));
	}

	@Test
	@DisplayName("경유지가 있으면 sequence 기반 경유 텍스트 생성")
	void handleShipmentCreated_withRoutes() {
		// given
		ShipmentCreatedEvent event = createEventWithRoutes("U123SLACK");

		OrderReadModelResponse order = new OrderReadModelResponse(
			event.getOrderId(),
			UUID.randomUUID().toString(),
			"마른 오징어",
			"경기 북부 센터",
			"부산광역시 센터"
		);

		AiLogResult aiResult = mock(AiLogResult.class);
		when(aiResult.aiId()).thenReturn(UUID.randomUUID());
		when(aiResult.finalDeadlineAt()).thenReturn(LocalDateTime.now());

		AiLog aiLog = new AiLog(UUID.randomUUID(), UUID.randomUUID(), "prompt", AiRequestType.DEADLINE);
		aiLog.markSuccess("response", LocalDateTime.now());

		when(orderInternalClient.getOrderReadModel(any())).thenReturn(order);
		when(aiAppService.generateAiLog(any())).thenReturn(aiResult);
		when(aiLogRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(aiLog));

		// when & then
		assertThatNoException()
			.isThrownBy(() -> notificationOrchestratorService.handleShipmentCreated(event));

		verify(aiAppService).generateAiLog(argThat(command ->
			command.route().contains("1번 경유") && command.route().contains("2번 경유")
		));
	}

	// ── 픽스처 ──────────────────────────────────────

	private ShipmentCreatedEvent createEvent(String slackId) {
		// reflection으로 필드 세팅 (getter only라서)
		ShipmentCreatedEvent event = new ShipmentCreatedEvent(
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID(),
			10,
			UUID.randomUUID(),
			UUID.randomUUID(),
			LocalDateTime.now().plusDays(3),
			"빨리 보내주세요",
			slackId,
			null
		);
		return event;
	}

	private ShipmentCreatedEvent createEventWithRoutes(String slackId) {
		ShipmentCreatedEvent event = new ShipmentCreatedEvent(
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID(),
			10,
			UUID.randomUUID(),
			UUID.randomUUID(),
			LocalDateTime.now().plusDays(3),
			"빨리 보내주세요",
			slackId,
			List.of(
				new ShipmentCreatedEvent.RouteInfo(1, UUID.randomUUID(), UUID.randomUUID()),
				new ShipmentCreatedEvent.RouteInfo(2, UUID.randomUUID(), UUID.randomUUID())
			)
		);
		return event;
	}
}