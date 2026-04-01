package com.shipflow.notificationservice.presentation.slack.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.notificationservice.domain.slack.SlackMessage;
import com.shipflow.notificationservice.domain.slack.SlackMessageType;
import com.shipflow.notificationservice.domain.slack.SlackSendStatus;

public record SlackMessageResponse(
	UUID id,
	String receiverSlackId,
	UUID relatedShipmentId,
	UUID relatedAiLogId,
	String slackTs,
	String slackChannelId,
	String message,
	SlackMessageType messageType,
	SlackSendStatus sendStatus,
	LocalDateTime sentAt
) {
	public static SlackMessageResponse from(SlackMessage slackMessage) {
		return new SlackMessageResponse(
			slackMessage.getId(),
			slackMessage.getReceiverSlackId(),
			slackMessage.getRelatedShipmentId(),
			slackMessage.getRelatedAiLogId(),
			slackMessage.getSlackTs(),
			slackMessage.getSlackChannelId(),
			slackMessage.getMessage(),
			slackMessage.getMessageType(),
			slackMessage.getSendStatus(),
			slackMessage.getSentAt()
		);
	}
}