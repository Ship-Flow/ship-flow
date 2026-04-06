package com.shipflow.shipmentservice.fixture;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.test.util.ReflectionTestUtils;

import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentRoute;
import com.shipflow.shipmentservice.domain.ShipmentRouteStatus;

public class ShipmentRouteFixture {

	public static ShipmentRoute createRoute(int sequence) {
		ShipmentManager manager = ShipmentManagerFixture.createHubManager();

		ShipmentRoute route = ShipmentRoute.create(
			sequence,
			UUID.randomUUID(),
			UUID.randomUUID(),
			new BigDecimal("12.50"),
			30,
			manager
		);

		ReflectionTestUtils.setField(route, "id", UUID.randomUUID());
		return route;
	}

	public static ShipmentRoute createRoute(UUID id, int sequence) {
		ShipmentManager manager = ShipmentManagerFixture.createHubManager();

		ShipmentRoute route = ShipmentRoute.create(
			sequence,
			UUID.randomUUID(),
			UUID.randomUUID(),
			new BigDecimal("12.50"),
			30,
			manager
		);

		ReflectionTestUtils.setField(route, "id", id);
		return route;
	}

	public static ShipmentRoute createMovingRoute(UUID id, int sequence, LocalDateTime updatedAt) {
		ShipmentRoute route = createRoute(id, sequence);
		ReflectionTestUtils.setField(route, "status", ShipmentRouteStatus.MOVING_TO_HUB);
		ReflectionTestUtils.setField(route, "updatedAt", updatedAt);
		return route;
	}

	public static ShipmentRoute createArrivedRoute(UUID id, int sequence, LocalDateTime updatedAt) {
		ShipmentRoute route = createRoute(id, sequence);
		ReflectionTestUtils.setField(route, "status", ShipmentRouteStatus.ARRIVED_AT_HUB);
		ReflectionTestUtils.setField(route, "updatedAt", updatedAt);
		return route;
	}
}
