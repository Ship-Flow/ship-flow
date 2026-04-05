package com.shipflow.shipmentservice.domain;

import java.util.UUID;

import com.shipflow.common.domain.BaseEntity;
import com.shipflow.common.exception.BusinessException;
import com.shipflow.shipmentservice.domain.exception.ShipmentErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_shipment_manager")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShipmentManager extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@Column(nullable = false, length = 20)
	private String name;

	@Column(name = "hub_id")
	private UUID hubId;

	@Column(name = "slack_id", nullable = false, length = 30)
	private String slackId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ShipmentManagerType type;

	@Column(name = "shipment_sequence", nullable = false)
	private Integer shipmentSequence;

	public void updateSequence(int sequence) {
		this.shipmentSequence = sequence;
	}

	public void delete(UUID deletedBy) {
		softDelete(deletedBy);
	}

	private ShipmentManager(
		UUID userId,
		String name,
		UUID hubId,
		String slackId,
		ShipmentManagerType type,
		Integer shipmentSequence
	) {
		this.userId = userId;
		this.name = name;
		this.hubId = hubId;
		this.slackId = slackId;
		this.type = type;
		this.shipmentSequence = shipmentSequence;
	}

	// 업체 배송 담당자 생성
	public static ShipmentManager createCompanyManager(
		UUID userId,
		String name,
		UUID hubId,
		String slackId,
		Integer shipmentSequence
	) {

		if (hubId == null) {
			throw new BusinessException(ShipmentErrorCode.HUB_ID_REQUIRED_FOR_COMPANY_MANAGER);
		}

		return new ShipmentManager(
			userId,
			name,
			hubId,
			slackId,
			ShipmentManagerType.COMPANY,
			shipmentSequence
		);
	}

	// 허브 배송 담당자 생성
	public static ShipmentManager createHubManager(
		UUID userId,
		String name,
		String slackId,
		Integer shipmentSequence
	) {

		return new ShipmentManager(
			userId,
			name,
			null,
			slackId,
			ShipmentManagerType.HUB,
			shipmentSequence
		);
	}

	private static void validateCommon(
		UUID userId,
		String name,
		String slackId,
		ShipmentManagerType type,
		Integer shipmentSequence
	) {
		if (userId == null) {
			throw new BusinessException(ShipmentErrorCode.SHIPMENT_MANAGER_USER_ID_REQUIRED);
		}
		if (type == null) {
			throw new BusinessException(ShipmentErrorCode.SHIPMENT_MANAGER_TYPE_REQUIRED);
		}
		if (name == null || name.isBlank()) {
			throw new BusinessException(ShipmentErrorCode.SHIPMENT_MANAGER_NAME_REQUIRED);
		}
		if (slackId == null || slackId.isBlank()) {
			throw new BusinessException(ShipmentErrorCode.SHIPMENT_MANAGER_SLACK_ID_REQUIRED);
		}
		if (shipmentSequence == null || shipmentSequence < 0) {
			throw new BusinessException(ShipmentErrorCode.INVALID_SHIPMENT_SEQUENCE);
		}
	}
}
