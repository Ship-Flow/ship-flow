package com.shipflow.notificationservice.application.ai.dto.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.shipflow.notificationservice.domain.ai.type.AiRequestType;

public record GenerateDeadlineCommand(
	UUID relatedShipmentId,
	UUID shipmentManagerId,
	String fromHub,
	String toHub,
	List<String> route,
	String product,
	String requestNote,
	LocalDateTime deadline,
	String workingHours,
	AiRequestType requestType,
	LocalDate workDate
) {
}
