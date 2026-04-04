package com.shipflow.notificationservice.domain.ai;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.common.domain.BaseEntity;
import com.shipflow.notificationservice.domain.ai.type.AiRequestStatus;
import com.shipflow.notificationservice.domain.ai.type.AiRequestType;
import com.shipflow.notificationservice.domain.slack.type.SlackSendStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "p_ai_log", schema = "notification")
public class AiLog extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "related_shipment_id")
	private UUID relatedShipmentId;

	@Column(name = "shipment_manager_id")
	private UUID shipmentManagerId;

	@Column(name = "prompt", columnDefinition = "TEXT", nullable = false)
	private String prompt;

	@Column(name = "response_text", columnDefinition = "TEXT")
	private String responseText;

	@Column(name = "final_deadline_at")
	private LocalDateTime finalDeadlineAt;

	@Column(name = "work_date")
	private LocalDate workDate;
	@Enumerated(EnumType.STRING)
	@Column(name = "send_status", length = 20, nullable = false)
	private SlackSendStatus sendStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "request_type", length = 20, nullable = false)
	private AiRequestType requestType;

	@Enumerated(EnumType.STRING)
	@Column(name = "request_status", length = 20, nullable = false)
	private AiRequestStatus requestStatus;

	protected AiLog() {
	}

	public AiLog(UUID relatedShipmentId,
		UUID shipmentManagerId,
		String prompt,
		AiRequestType requestType) {

		this.relatedShipmentId = relatedShipmentId;
		this.shipmentManagerId = shipmentManagerId;
		this.prompt = prompt;
		this.requestType = requestType;

		this.requestStatus = AiRequestStatus.PENDING;
		this.sendStatus = SlackSendStatus.PENDING;
	}

	//AI 성공
	public void markSuccess(String responseText, LocalDateTime finalDeadlineAt) {
		this.responseText = responseText;
		this.finalDeadlineAt = finalDeadlineAt;
		this.requestStatus = AiRequestStatus.SUCCESS;
	}

	//AI 실패
	public void markFail() {
		this.requestStatus = AiRequestStatus.FAIL;
	}

	//슬랙 발송 성공
	public void markSendSuccess() {
		this.sendStatus = SlackSendStatus.SUCCESS;
	}

	// 슬랙 발송 실패
	public void markSendFail() {
		this.sendStatus = SlackSendStatus.FAIL;
	}

} //끝
