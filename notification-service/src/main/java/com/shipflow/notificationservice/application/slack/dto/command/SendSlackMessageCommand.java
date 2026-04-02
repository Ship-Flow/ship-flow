package com.shipflow.notificationservice.application.slack.dto.command;

import java.util.UUID;

import com.shipflow.notificationservice.domain.slack.SlackMessageType;

public record SendSlackMessageCommand(
	String receiverSlackId,
	UUID relatedShipmentId,
	UUID relatedAiLogId,
	String message,
	SlackMessageType messageType
) {
}