package com.shipflow.notificationservice.application.slack.dto.command;

import java.util.UUID;

import com.shipflow.notificationservice.domain.slack.type.SlackMessageType;

public record SendSlackMessageCommand(
	UUID userId,
	String userRole,
	String receiverSlackId,
	UUID relatedShipmentId,
	UUID relatedAiLogId,
	String message,
	SlackMessageType messageType
) {
}