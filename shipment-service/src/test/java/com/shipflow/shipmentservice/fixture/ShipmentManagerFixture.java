package com.shipflow.shipmentservice.fixture;

import java.util.UUID;

import org.springframework.test.util.ReflectionTestUtils;

import com.shipflow.shipmentservice.domain.ShipmentManager;

public class ShipmentManagerFixture {

	public static ShipmentManager createCompanyManager() {
		ShipmentManager manager = ShipmentManager.createCompanyManager(
			UUID.randomUUID(),
			"업체담당자",
			UUID.randomUUID(),
			"company-slack",
			1
		);
		ReflectionTestUtils.setField(manager, "id", UUID.randomUUID());
		return manager;
	}

	public static ShipmentManager createHubManager() {
		ShipmentManager manager = ShipmentManager.createHubManager(
			UUID.randomUUID(),
			"허브담당자",
			"hub-slack",
			1
		);
		ReflectionTestUtils.setField(manager, "id", UUID.randomUUID());
		return manager;
	}

	public static ShipmentManager createHubManager(UUID id) {
		ShipmentManager manager = ShipmentManager.createHubManager(
			UUID.randomUUID(),
			"허브담당자",
			"hub-slack",
			1
		);
		ReflectionTestUtils.setField(manager, "id", id);
		return manager;
	}
}
