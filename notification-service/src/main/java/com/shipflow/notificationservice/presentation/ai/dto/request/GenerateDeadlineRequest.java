package com.shipflow.notificationservice.presentation.ai.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.shipflow.notificationservice.application.ai.dto.command.GenerateDeadlineCommand;
import com.shipflow.notificationservice.domain.ai.type.AiRequestType;

import jakarta.validation.constraints.NotNull;

public record GenerateDeadlineRequest(
	@NotNull UUID orderId,
	@NotNull UUID relatedShipmentId,
	@NotNull UUID shipmentManagerId,
	@NotNull String receiverSlackId,
	@NotNull UUID productId,
	@NotNull String product,
	@NotNull Integer quantity,
	@NotNull UUID departureHubId,
	@NotNull String fromHub,
	@NotNull UUID arrivalHubId,
	@NotNull String toHub,
	List<String> route,
	String requestNote,
	@NotNull LocalDateTime deadline,
	String workingHours
) {
	public GenerateDeadlineCommand toCommand(UUID ordererId) {
		return new GenerateDeadlineCommand(
			orderId,
			ordererId,
			relatedShipmentId,
			shipmentManagerId,
			receiverSlackId,
			null,
			null,
			productId,
			product,
			quantity,
			departureHubId,
			fromHub,
			arrivalHubId,
			toHub,
			route,
			requestNote,
			deadline,
			workingHours,
			AiRequestType.DEADLINE,
			null
		);
	}
}