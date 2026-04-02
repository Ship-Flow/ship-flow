package com.shipflow.shipmentservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.shipflow.shipmentservice.domain.Shipment;

public interface ShipmentRepository {
	Optional<Shipment> findById(UUID shipmentId);
}
