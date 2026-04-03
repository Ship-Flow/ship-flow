package com.shipflow.notificationservice.application.slack.dto.command;

import java.util.UUID;

public record UpdateSlackMessageCommand(
	UUID slackId,
	String message
) {
}