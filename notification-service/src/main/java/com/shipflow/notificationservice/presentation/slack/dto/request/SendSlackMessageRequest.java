package com.shipflow.notificationservice.presentation.slack.dto.request;

import java.util.UUID;

import com.shipflow.notificationservice.application.slack.dto.command.SendSlackMessageCommand;
import com.shipflow.notificationservice.domain.slack.type.SlackMessageType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SendSlackMessageRequest(
	@NotBlank
	@Pattern(regexp = "^[UC][A-Z0-9]+$", message = "올바른 Slack ID 형식이 아닙니다.")
	String receiverSlackId,

	UUID relatedShipmentId,
	UUID relatedAiLogId,

	@NotBlank
	@Size(max = 1000)
	String message,

	@NotNull
	SlackMessageType messageType
) {
	public SendSlackMessageCommand toCommand(UUID userId, String userRole) {
		return new SendSlackMessageCommand(
			userId,
			userRole,
			receiverSlackId,
			relatedShipmentId,
			relatedAiLogId,
			message,
			messageType
		);
	}
}