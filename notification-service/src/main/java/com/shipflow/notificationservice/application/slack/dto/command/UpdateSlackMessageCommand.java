package com.shipflow.notificationservice.application.slack.dto.command;

import java.util.UUID;

public record UpdateSlackMessageCommand(
	UUID userId,
	String userRole,
	UUID slackId,
	String message
) {
}