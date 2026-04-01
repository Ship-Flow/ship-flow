package com.shipflow.notificationservice.domain.slack;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "p_slack", schema = "notification")
public class SlackMessage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "receiver_slack_id", length = 100)
	private String receiverSlackId;

	@Column(name = "related_shipment_id")
	private UUID relatedShipmentId;

	@Column(name = "related_ai_log_id")
	private UUID relatedAiLogId;

	@Column(name = "slack_ts", length = 50)
	private String slackTs;

	@Column(name = "slack_channel_id", length = 50)
	private String slackChannelId;

	@Column(name = "message", columnDefinition = "TEXT")
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(name = "message_type", length = 30)
	private SlackMessageType messageType;

	@Enumerated(EnumType.STRING)
	@Column(name = "send_status", length = 20, nullable = false)
	private SlackSendStatus sendStatus;

	@Column(name = "sent_at")
	private LocalDateTime sentAt;

	protected SlackMessage() {
	}

	public SlackMessage(
		String receiverSlackId,
		UUID relatedShipmentId,
		UUID relatedAiLogId,
		String message,
		SlackMessageType messageType
	) {
		this.receiverSlackId = receiverSlackId;
		this.relatedShipmentId = relatedShipmentId;
		this.relatedAiLogId = relatedAiLogId;
		this.message = message;
		this.messageType = messageType;
		this.sendStatus = SlackSendStatus.PENDING;
	}

	public void markSuccess(String slackTs, String slackChannelId) {
		this.sendStatus = SlackSendStatus.SUCCESS;
		this.slackTs = slackTs;
		this.slackChannelId = slackChannelId;
		this.sentAt = LocalDateTime.now();
	}

	public void markFail() {
		this.sendStatus = SlackSendStatus.FAIL;
	}

	public UUID getId() {
		return id;
	}

	public String getReceiverSlackId() {
		return receiverSlackId;
	}

	public UUID getRelatedShipmentId() {
		return relatedShipmentId;
	}

	public UUID getRelatedAiLogId() {
		return relatedAiLogId;
	}

	public String getSlackTs() {
		return slackTs;
	}

	public String getSlackChannelId() {
		return slackChannelId;
	}

	public String getMessage() {
		return message;
	}

	public SlackMessageType getMessageType() {
		return messageType;
	}

	public SlackSendStatus getSendStatus() {
		return sendStatus;
	}

	public LocalDateTime getSentAt() {
		return sentAt;
	}
}
