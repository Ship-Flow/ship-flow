package com.shipflow.notificationservice.presentation.slack.dto;

import java.util.UUID;

import com.shipflow.notificationservice.domain.slack.SlackMessageType;

public record SlackSendRequest(
	String receiverSlackId,
	UUID relatedShipmentId,
	UUID relatedAiLogId,
	String message,
	SlackMessageType messageType
) {
}