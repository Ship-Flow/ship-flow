package com.shipflow.notificationservice.application.ai.dto.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.shipflow.notificationservice.domain.ai.type.AiRequestType;

public record GenerateDeadlineCommand(
	UUID orderId,
	UUID ordererId,
	UUID relatedShipmentId,
	UUID shipmentManagerId,
	String receiverSlackId,

	UUID supplierCompanyId,
	UUID receiverCompanyId,

	UUID productId,
	String product,
	Integer quantity,

	UUID departureHubId,
	String fromHub,
	UUID arrivalHubId,
	String toHub,
	List<String> route,

	String requestNote,
	LocalDateTime deadline,
	String workingHours,

	AiRequestType requestType,
	LocalDate workDate
) {
}