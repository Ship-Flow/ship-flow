package com.shipflow.notificationservice.application.ai.dto.result;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.notificationservice.domain.ai.AiLog;
import com.shipflow.notificationservice.domain.ai.type.AiRequestStatus;
import com.shipflow.notificationservice.domain.ai.type.AiRequestType;
import com.shipflow.notificationservice.domain.slack.type.SlackSendStatus;

public record AiLogResult(
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
	public static AiLogResult from(AiLog aiLog) {
		return new AiLogResult(
			aiLog.getId(),
			aiLog.getRelatedShipmentId(),
			aiLog.getShipmentManagerId(),
			aiLog.getPrompt(),
			aiLog.getResponseText(),
			aiLog.getFinalDeadlineAt(),
			aiLog.getWorkDate(),
			aiLog.getSendStatus(),
			aiLog.getRequestType(),
			aiLog.getRequestStatus()
		);
	}
}
