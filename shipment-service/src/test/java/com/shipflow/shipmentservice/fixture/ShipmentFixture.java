package com.shipflow.shipmentservice.fixture;

import java.util.List;
import java.util.UUID;

import org.springframework.test.util.ReflectionTestUtils;

import com.shipflow.shipmentservice.domain.Shipment;
import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentRoute;
import com.shipflow.shipmentservice.domain.ShipmentStatus;

public class ShipmentFixture {

	public static Shipment createShipment() {
		ShipmentManager manager = ShipmentManagerFixture.createCompanyManager();
		ShipmentRoute route1 = ShipmentRouteFixture.createRoute(1);
		ShipmentRoute route2 = ShipmentRouteFixture.createRoute(2);

		Shipment shipment = Shipment.create(
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID(),
			"서울시 강남구 테헤란로 123",
			"홍길동",
			"hong123",
			manager,
			List.of(route1, route2)
		);

		ReflectionTestUtils.setField(shipment, "id", UUID.randomUUID());
		return shipment;
	}

	public static Shipment createShipment(UUID shipmentId) {
		ShipmentManager manager = ShipmentManagerFixture.createCompanyManager();
		ShipmentRoute route1 = ShipmentRouteFixture.createRoute(1);
		ShipmentRoute route2 = ShipmentRouteFixture.createRoute(2);

		Shipment shipment = Shipment.create(
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID(),
			"서울시 강남구 테헤란로 123",
			"홍길동",
			"hong123",
			manager,
			List.of(route1, route2)
		);

		ReflectionTestUtils.setField(shipment, "id", shipmentId);
		return shipment;
	}

	public static Shipment createShipmentWithRoutes(UUID shipmentId, List<ShipmentRoute> routes) {
		ShipmentManager manager = ShipmentManagerFixture.createCompanyManager();

		Shipment shipment = Shipment.create(
			UUID.randomUUID(),
			UUID.randomUUID(),
			UUID.randomUUID(),
			"서울시 강남구 테헤란로 123",
			"홍길동",
			"hong123",
			manager,
			routes
		);

		ReflectionTestUtils.setField(shipment, "id", shipmentId);
		return shipment;
	}

	public static Shipment createShipmentWithStatus(UUID shipmentId, ShipmentStatus status) {
		Shipment shipment = createShipment(shipmentId);
		ReflectionTestUtils.setField(shipment, "status", status);
		return shipment;
	}
}
