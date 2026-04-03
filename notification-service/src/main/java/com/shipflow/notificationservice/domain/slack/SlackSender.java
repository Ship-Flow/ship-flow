package com.shipflow.notificationservice.domain.slack;

import com.shipflow.notificationservice.domain.slack.vo.SlackDeleteResult;
import com.shipflow.notificationservice.domain.slack.vo.SlackSendResult;
import com.shipflow.notificationservice.domain.slack.vo.SlackUpdateResult;

public interface SlackSender {

	SlackSendResult sendMessage(String receiverSlackId, String message);

	SlackUpdateResult updateMessage(String channelId, String ts, String message);

	SlackDeleteResult deleteMessage(String channelId, String ts);
}