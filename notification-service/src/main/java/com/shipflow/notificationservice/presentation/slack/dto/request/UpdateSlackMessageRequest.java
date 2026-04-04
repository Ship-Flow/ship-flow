package com.shipflow.notificationservice.presentation.slack.dto.request;

import java.util.UUID;

import com.shipflow.notificationservice.application.slack.dto.command.UpdateSlackMessageCommand;

public record UpdateSlackMessageRequest(
	String message
) {
	public UpdateSlackMessageCommand toCommand(UUID slackId) {
		return new UpdateSlackMessageCommand(slackId, message);
	}
}