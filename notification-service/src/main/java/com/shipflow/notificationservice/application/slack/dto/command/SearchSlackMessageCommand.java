package com.shipflow.notificationservice.application.slack.dto.command;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.notificationservice.domain.slack.type.SlackMessageType;
import com.shipflow.notificationservice.domain.slack.type.SlackSendStatus;

public record SearchSlackMessageCommand(
	UUID userId,
	String userRole,
	String receiverSlackId,
	SlackSendStatus sendStatus,
	SlackMessageType messageType,
	LocalDateTime createdAtFrom,
	LocalDateTime createdAtTo
) {
}