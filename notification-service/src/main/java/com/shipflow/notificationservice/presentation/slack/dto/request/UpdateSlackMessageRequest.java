package com.shipflow.notificationservice.presentation.slack.dto.request;

import java.util.UUID;

import com.shipflow.notificationservice.application.slack.dto.command.UpdateSlackMessageCommand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSlackMessageRequest(
	@NotBlank
	@Size(max = 1000)
	String message
) {
	public UpdateSlackMessageCommand toCommand(UUID slackId) {
		return new UpdateSlackMessageCommand(slackId, message);
	}
}