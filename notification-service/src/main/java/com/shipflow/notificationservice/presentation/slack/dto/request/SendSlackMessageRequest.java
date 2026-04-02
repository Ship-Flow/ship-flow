package com.shipflow.notificationservice.presentation.slack.dto.request;

import java.util.UUID;

import com.shipflow.notificationservice.application.slack.dto.command.SendSlackMessageCommand;
import com.shipflow.notificationservice.domain.slack.SlackMessageType;

public record SendSlackMessageRequest(
	String receiverSlackId,
	UUID relatedShipmentId,
	UUID relatedAiLogId,
	String message,
	SlackMessageType messageType
) {
	public SendSlackMessageCommand toCommand() {
		return new SendSlackMessageCommand(
			receiverSlackId,
			relatedShipmentId,
			relatedAiLogId,
			message,
			messageType
		);
	}
}