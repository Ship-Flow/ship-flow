package com.shipflow.shipmentservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.shipflow.shipmentservice.application.dto.result.ShipmentManagerResult;
import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;

public interface ShipmentManagerRepository {

	ShipmentManager save(ShipmentManager shipmentManager);

	int findMaxSequenceByType(ShipmentManagerType type);

	int findMaxSequenceByTypeAndHubId(ShipmentManagerType type, UUID hubId);

	Optional<ShipmentManager> findById(UUID managerId);
}
