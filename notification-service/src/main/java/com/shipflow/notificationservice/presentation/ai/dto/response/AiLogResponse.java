package com.shipflow.notificationservice.presentation.ai.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.notificationservice.application.ai.dto.result.AiLogResult;
import com.shipflow.notificationservice.domain.ai.type.AiRequestStatus;
import com.shipflow.notificationservice.domain.ai.type.AiRequestType;
import com.shipflow.notificationservice.domain.slack.type.SlackSendStatus;

public record AiLogResponse(
	UUID aiId,
	UUID relatedShipmentId,
	UUID shipmentManagerId,
	String prompt,
	String responseText,
	LocalDateTime finalDeadlineAt,
	LocalDate workDate,
	SlackSendStatus sendStatus,
	AiRequestType requestType,
	AiRequestStatus requestStatus
) {

	public static AiLogResponse from(AiLogResult result) {
		return new AiLogResponse(
			result.aiId(),
			result.relatedShipmentId(),
			result.shipmentManagerId(),
			result.prompt(),
			result.responseText(),
			result.finalDeadlineAt(),
			result.workDate(),
			result.sendStatus(),
			result.requestType(),
			result.requestStatus()
		);
	}
}