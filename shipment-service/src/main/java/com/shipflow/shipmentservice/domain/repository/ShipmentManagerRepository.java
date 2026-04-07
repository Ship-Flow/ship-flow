package com.shipflow.shipmentservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.shipflow.shipmentservice.application.dto.query.ShipmentManagerSearchQuery;
import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;

public interface ShipmentManagerRepository {

	ShipmentManager save(ShipmentManager shipmentManager);

	List<ShipmentManager> findAll(ShipmentManagerSearchQuery query, Pageable pageable);

	int findMaxSequenceByType(ShipmentManagerType type);

	int findMaxSequenceByTypeAndHubId(ShipmentManagerType type, UUID hubId);

	Optional<ShipmentManager> findById(UUID managerId);

	Optional<ShipmentManager> findFirstAvailableByType(ShipmentManagerType type);

	List<ShipmentManager> findAllByType(ShipmentManagerType type);

	List<ShipmentManager> findAllByHubId(UUID hubId);

	Optional<ShipmentManager> findByUserId(UUID userId);

	List<ShipmentManager> findAllPendingDeletion();
}
