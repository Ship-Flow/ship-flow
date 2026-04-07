package com.shipflow.notificationservice.presentation.ai.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.shipflow.notificationservice.application.ai.dto.command.GenerateDeadlineCommand;
import com.shipflow.notificationservice.domain.ai.type.AiRequestType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GenerateDeadlineRequest(
	@NotNull UUID orderId,
	@NotNull UUID relatedShipmentId,
	UUID shipmentManagerId,              // @NotNull 제거 - 이벤트에서 못받음
	@NotBlank String receiverSlackId,    // @NotNull -> @NotBlank
	@NotNull UUID productId,
	@NotBlank String product,            // @NotNull -> @NotBlank
	@NotNull Integer quantity,
	@NotNull UUID departureHubId,
	@NotBlank String fromHub,            // @NotNull -> @NotBlank
	@NotNull UUID arrivalHubId,
	@NotBlank String toHub,              // @NotNull -> @NotBlank
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