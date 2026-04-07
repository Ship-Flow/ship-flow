package com.shipflow.notificationservice.presentation.slack.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.notificationservice.application.slack.dto.result.SlackMessageResult;
import com.shipflow.notificationservice.domain.slack.type.SlackMessageType;
import com.shipflow.notificationservice.domain.slack.type.SlackSendStatus;

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
	public static SlackMessageResponse from(SlackMessageResult result) {
		return new SlackMessageResponse(
			result.slackId(),
			result.receiverSlackId(),
			result.relatedShipmentId(),
			result.relatedAiLogId(),
			result.slackTs(),
			result.slackChannelId(),
			result.message(),
			result.messageType(),
			result.sendStatus(),
			result.sentAt()
		);
	}

}