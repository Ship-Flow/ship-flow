package com.shipflow.notificationservice.application.ai.dto.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.notificationservice.domain.ai.type.AiRequestStatus;
import com.shipflow.notificationservice.domain.ai.type.AiRequestType;

public record SearchAiLogCommand(
	UUID userId,
	String userRole,
	UUID shipmentManagerId,
	AiRequestType requestType,
	AiRequestStatus requestStatus,
	LocalDate workDate,
	LocalDateTime createdAtFrom,
	LocalDateTime createdAtTo
) {
}
