package com.shipflow.shipmentservice.domain.repository;

import java.util.UUID;

import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;

public interface ShipmentManagerRepository {

	ShipmentManager save(ShipmentManager shipmentManager);

	int findNextSequenceByType(ShipmentManagerType type);

	int findNextSequenceByTypeAndHubId(ShipmentManagerType type, UUID hubId);
}
