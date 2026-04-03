package com.shipflow.shipmentservice.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.shipflow.common.domain.BaseEntity;

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

}
