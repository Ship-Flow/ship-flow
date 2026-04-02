package com.shipflow.notificationservice.domain.slack;

import com.shipflow.notificationservice.infrastructure.client.slack.dto.SlackDeleteResult;
import com.shipflow.notificationservice.infrastructure.client.slack.dto.SlackSendResult;
import com.shipflow.notificationservice.infrastructure.client.slack.dto.SlackUpdateResult;

public interface SlackSender {
	
	SlackSendResult sendMessage(String receiverSlackId, String message);

	SlackUpdateResult updateMessage(String channelId, String ts, String message);

	SlackDeleteResult deleteMessage(String channelId, String ts);
}