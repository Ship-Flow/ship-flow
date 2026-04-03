package com.shipflow.notificationservice.application.slack.dto.result;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.notificationservice.domain.slack.SlackMessage;
import com.shipflow.notificationservice.domain.slack.type.SlackMessageType;
import com.shipflow.notificationservice.domain.slack.type.SlackSendStatus;

public record SlackMessageResult(
	UUID slackId,
	String receiverSlackId,
	UUID relatedShipmentId,
	UUID relatedAiLogId,
	String slackTs,
	String slackChannelId,
	String message,
	SlackMessageType messageType,
	SlackSendStatus sendStatus,
	LocalDateTime sentAt,
	LocalDateTime createdAt
) {
	public static SlackMessageResult from(SlackMessage slackMessage) {
		return new SlackMessageResult(
			slackMessage.getId(),
			slackMessage.getReceiverSlackId(),
			slackMessage.getRelatedShipmentId(),
			slackMessage.getRelatedAiLogId(),
			slackMessage.getSlackTs(),
			slackMessage.getSlackChannelId(),
			slackMessage.getMessage(),
			slackMessage.getMessageType(),
			slackMessage.getSendStatus(),
			slackMessage.getSentAt(),
			slackMessage.getCreatedAt()
		);
	}
}