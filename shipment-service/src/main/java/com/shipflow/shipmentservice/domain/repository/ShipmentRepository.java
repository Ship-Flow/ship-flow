package com.shipflow.shipmentservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.shipflow.shipmentservice.domain.Shipment;

public interface ShipmentRepository {
	Optional<Shipment> findById(UUID shipmentId);

	Optional<Shipment> findByIdWithManager(UUID shipmentId);

	List<Shipment> findAll(Pageable pageable);

	Optional<Shipment> findByIdWithRoutes(UUID shipmentId);
}
