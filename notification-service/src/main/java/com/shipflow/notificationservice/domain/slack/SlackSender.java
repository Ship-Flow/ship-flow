package com.shipflow.notificationservice.domain.slack;

import com.shipflow.notificationservice.domain.slack.vo.SlackDeleteInfo;
import com.shipflow.notificationservice.domain.slack.vo.SlackSendInfo;
import com.shipflow.notificationservice.domain.slack.vo.SlackUpdateInfo;

public interface SlackSender {

	SlackSendInfo sendMessage(String receiverSlackId, String message);

	SlackUpdateInfo updateMessage(String channelId, String ts, String message);

	SlackDeleteInfo deleteMessage(String channelId, String ts);
}