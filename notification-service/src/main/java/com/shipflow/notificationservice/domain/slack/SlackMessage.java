package com.shipflow.notificationservice.domain.slack;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_slack", schema = "notification")
public class SlackMessage {

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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    protected SlackMessage() {
    }

    public SlackMessage(
            String receiverSlackId,
            UUID relatedShipmentId,
            UUID relatedAiLogId,
            String message,
            SlackMessageType messageType,
            UUID createdBy
    ) {
        this.receiverSlackId = receiverSlackId;
        this.relatedShipmentId = relatedShipmentId;
        this.relatedAiLogId = relatedAiLogId;
        this.message = message;
        this.messageType = messageType;
        this.sendStatus = SlackSendStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.createdBy = createdBy;
    }

    public void markSuccess(String slackTs, String slackChannelId) {
        this.sendStatus = SlackSendStatus.SUCCESS;
        this.slackTs = slackTs;
        this.slackChannelId = slackChannelId;
        this.sentAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markFail(UUID updatedBy) {
        this.sendStatus = SlackSendStatus.FAIL;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public UUID getDeletedBy() {
        return deletedBy;
    }
}