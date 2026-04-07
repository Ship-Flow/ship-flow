package com.shipflow.shipmentservice.domain;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.common.domain.BaseEntity;
import com.shipflow.common.exception.BusinessException;
import com.shipflow.shipmentservice.domain.exception.ShipmentErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "p_shipment_route",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_shipment_route_shipment_id_sequence",
			columnNames = {"shipment_id", "sequence"}
		)
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipmentRoute extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shipment_id", nullable = false)
	private Shipment shipment;

	@Column(name = "sequence", nullable = false)
	private Integer sequence;

	@Column(name = "departure_hub_id", nullable = false)
	private UUID departureHubId;

	@Column(name = "arrival_hub_id", nullable = false)
	private UUID arrivalHubId;

	@Column(name = "estimated_distance", nullable = false, precision = 10, scale = 2)
	private BigDecimal estimatedDistance;

	@Column(name = "estimated_duration", nullable = false)
	private Integer estimatedDuration;

	@Column(name = "actual_distance", precision = 10, scale = 2)
	private BigDecimal actualDistance;

	@Column(name = "actual_duration")
	private Integer actualDuration;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private ShipmentRouteStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shipment_manager_id", nullable = false)
	private ShipmentManager shipmentManager;

	private ShipmentRoute(
		Integer sequence,
		UUID departureHubId,
		UUID arrivalHubId,
		BigDecimal estimatedDistance,
		Integer estimatedDuration,
		ShipmentRouteStatus status,
		ShipmentManager shipmentManager
	) {
		this.sequence = sequence;
		this.departureHubId = departureHubId;
		this.arrivalHubId = arrivalHubId;
		this.estimatedDistance = estimatedDistance != null ? estimatedDistance : BigDecimal.ZERO;
		this.estimatedDuration = estimatedDuration != null ? estimatedDuration : 0;
		this.actualDistance = BigDecimal.ZERO;
		this.actualDuration = 0;
		this.shipmentManager = shipmentManager;
	}

	public static ShipmentRoute create(
		Integer sequence,
		UUID departureHubId,
		UUID arrivalHubId,
		BigDecimal estimatedDistance,
		Integer estimatedDuration,
		ShipmentManager shipmentManager
	) {
		return new ShipmentRoute(
			sequence,
			departureHubId,
			arrivalHubId,
			estimatedDistance,
			estimatedDuration,
			ShipmentRouteStatus.WAITING_AT_HUB,
			shipmentManager
		);
	}

	void assignShipment(Shipment shipment) {
		this.shipment = shipment;
	}

	public void markCanceled() {
		this.status = ShipmentRouteStatus.CANCELED;
	}

	public void markMovingToHub() {
		validateMovableToHub();
		this.status = ShipmentRouteStatus.MOVING_TO_HUB;
	}

	public void markArrivedAtHub(BigDecimal actualDistance, LocalDateTime baseTime) {
		validateArrivedAtHub(actualDistance, baseTime);

		LocalDateTime now = LocalDateTime.now();

		this.status = ShipmentRouteStatus.ARRIVED_AT_HUB;
		this.actualDistance = actualDistance;
		this.actualDuration = calculateActualDuration(baseTime, now);
	}

	private void validateMovableToHub() {
		if (this.status == ShipmentRouteStatus.MOVING_TO_HUB) {
			throw new BusinessException(ShipmentErrorCode.INVALID_SHIPMENT_ROUTE_STATUS);
		}

		if (this.status == ShipmentRouteStatus.ARRIVED_AT_HUB) {
			throw new BusinessException(ShipmentErrorCode.INVALID_SHIPMENT_ROUTE_STATUS);
		}
	}

	private void validateArrivedAtHub(BigDecimal actualDistance, LocalDateTime baseTime) {
		if (this.status != ShipmentRouteStatus.MOVING_TO_HUB) {
			throw new BusinessException(ShipmentErrorCode.INVALID_SHIPMENT_ROUTE_STATUS);
		}

		if (actualDistance == null || actualDistance.signum() <= 0) {
			throw new BusinessException(ShipmentErrorCode.INVALID_ACTUAL_DISTANCE);
		}

		if (baseTime == null) {
			throw new BusinessException(ShipmentErrorCode.INVALID_PREVIOUS_ROUTE_TIME);
		}
	}

	private int calculateActualDuration(LocalDateTime baseTime, LocalDateTime now) {
		long minutes = Duration.between(baseTime, now).toMinutes();

		if (minutes < 0) {
			throw new BusinessException(ShipmentErrorCode.INVALID_DURATION);
		}

		return (int)minutes;
	}
}
