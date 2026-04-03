package com.shipflow.shipmentservice.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.shipflow.common.domain.BaseEntity;
import com.shipflow.common.exception.BusinessException;
import com.shipflow.shipmentservice.domain.exception.ShipmentErrorCode;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_shipment")
public class Shipment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "order_id", nullable = false)
	private UUID orderId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ShipmentStatus status;

	@Column(name = "departure_hub_id", nullable = false)
	private UUID departureHubId;

	@Column(name = "arrival_hub_id", nullable = false)
	private UUID arrivalHubId;

	@Column(name = "shipment_address", nullable = false, length = 100)
	private String shipmentAddress;

	@Column(name = "recipient_name", nullable = false, length = 50)
	private String recipientName;

	@Column(name = "recipient_slack_id", nullable = false, length = 30)
	private String recipientSlackId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shipment_manager_id", nullable = false)
	private ShipmentManager shipmentManager;

	@OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ShipmentRoute> routes = new ArrayList<>();

	private Shipment(
		UUID orderId,
		ShipmentStatus status,
		UUID departureHubId,
		UUID arrivalHubId,
		String shipmentAddress,
		String recipientName,
		String recipientSlackId,
		ShipmentManager shipmentManager
	) {
		this.orderId = orderId;
		this.status = status;
		this.departureHubId = departureHubId;
		this.arrivalHubId = arrivalHubId;
		this.shipmentAddress = shipmentAddress;
		this.recipientName = recipientName;
		this.recipientSlackId = recipientSlackId;
		this.shipmentManager = shipmentManager;
	}

	public static Shipment create(
		UUID orderId,
		UUID departureHubId,
		UUID arrivalHubId,
		String shipmentAddress,
		String recipientName,
		String recipientSlackId,
		ShipmentManager shipmentManager,
		List<ShipmentRoute> routes
	) {
		Shipment shipment = new Shipment(
			orderId,
			ShipmentStatus.WAITING_AT_HUB,
			departureHubId,
			arrivalHubId,
			shipmentAddress,
			recipientName,
			recipientSlackId,
			shipmentManager
		);

		for (ShipmentRoute route : routes) {
			shipment.addRoute(route);
		}

		return shipment;
	}

	public void addRoute(ShipmentRoute route) {
		this.routes.add(route);
		route.assignShipment(this);
	}

	public void updateStatus(ShipmentStatus status) {
		this.status = status;
	}

	public ShipmentRoute markRouteMovingToHub(UUID routeId) {
		ShipmentRoute route = getRoute(routeId);
		route.markMovingToHub();
		return route;
	}

	public ShipmentRoute markRouteArrivedAtHub(UUID routeId, BigDecimal actualDistance) {
		ShipmentRoute route = getRoute(routeId);
		LocalDateTime baseTime = getArrivalBaseTime(route);

		route.markArrivedAtHub(actualDistance, baseTime);

		return route;
	}

	private LocalDateTime getArrivalBaseTime(ShipmentRoute currentRoute) {
		ShipmentRoute previousRoute = routes.stream()
			.filter(r -> r.getSequence() < currentRoute.getSequence())
			.max((a, b) -> Integer.compare(a.getSequence(), b.getSequence()))
			.orElse(null);

		// 첫 번째 경로인 경우: 현재 경로가 MOVING_TO_HUB 로 변경된 시점 기준
		if (previousRoute == null) {
			if (currentRoute.getStatus() != ShipmentRouteStatus.MOVING_TO_HUB) {
				throw new BusinessException(ShipmentErrorCode.INVALID_SHIPMENT_ROUTE_STATUS);
			}

			if (currentRoute.getUpdatedAt() == null) {
				throw new BusinessException(ShipmentErrorCode.INVALID_PREVIOUS_ROUTE_TIME);
			}

			return currentRoute.getUpdatedAt();
		}

		// 이전 경로가 있는 경우: 이전 경로는 반드시 ARRIVED_AT_HUB 상태여야 함
		if (previousRoute.getStatus() != ShipmentRouteStatus.ARRIVED_AT_HUB) {
			throw new BusinessException(ShipmentErrorCode.PREVIOUS_ROUTE_NOT_COMPLETED);
		}

		if (previousRoute.getUpdatedAt() == null) {
			throw new BusinessException(ShipmentErrorCode.INVALID_PREVIOUS_ROUTE_TIME);
		}

		return previousRoute.getUpdatedAt();
	}

	private ShipmentRoute getRoute(UUID routeId) {
		return routes.stream()
			.filter(r -> r.getId().equals(routeId))
			.findFirst()
			.orElseThrow(() -> new BusinessException(ShipmentErrorCode.SHIPMENT_ROUTE_NOT_FOUND));
	}

	private LocalDateTime getPreviousRouteCompletedTime(ShipmentRoute currentRoute) {
		ShipmentRoute previousRoute = routes.stream()
			.filter(r -> r.getSequence() < currentRoute.getSequence())
			.max(Comparator.comparingInt(ShipmentRoute::getSequence))
			.orElseThrow(() -> new BusinessException(ShipmentErrorCode.PREVIOUS_ROUTE_NOT_FOUND));

		if (previousRoute.getStatus() != ShipmentRouteStatus.ARRIVED_AT_HUB) {
			throw new BusinessException(ShipmentErrorCode.PREVIOUS_ROUTE_NOT_COMPLETED);
		}

		if (previousRoute.getUpdatedAt() == null) {
			throw new BusinessException(ShipmentErrorCode.INVALID_PREVIOUS_ROUTE_TIME);
		}

		return previousRoute.getUpdatedAt();
	}

}
