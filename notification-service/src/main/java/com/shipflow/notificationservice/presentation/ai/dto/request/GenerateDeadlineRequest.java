package com.shipflow.notificationservice.presentation.ai.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.shipflow.notificationservice.application.ai.dto.command.GenerateDeadlineCommand;
import com.shipflow.notificationservice.domain.ai.type.AiRequestType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GenerateDeadlineRequest(

	@NotNull(message = "relatedShipmentId는 필수입니다.")
	UUID relatedShipmentId,

	@NotNull(message = "shipmentManagerId는 필수입니다.")
	UUID shipmentManagerId,

	@NotBlank(message = "fromHub는 필수입니다.")
	String fromHub,

	@NotBlank(message = "toHub는 필수입니다.")
	String toHub,

	List<String> route,

	@NotBlank(message = "product는 필수입니다.")
	String product,

	String requestNote,

	@NotNull(message = "deadline은 필수입니다.")
	LocalDateTime deadline,

	String workingHours,

	@NotNull(message = "requestType은 필수입니다.")
	AiRequestType requestType,

	@NotBlank(message = "receiverSlackId는 필수입니다.")
	String receiverSlackId,

	LocalDate workDate
) {

	public GenerateDeadlineCommand toCommand() {
		return new GenerateDeadlineCommand(
			relatedShipmentId,
			shipmentManagerId,
			fromHub,
			toHub,
			route,
			product,
			requestNote,
			deadline,
			workingHours,
			requestType,
			workDate
		);
	}
}