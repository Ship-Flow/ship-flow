package com.shipflow.shipmentservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shipflow.shipmentservice.domain.Shipment;

public interface ShipmentJpaRepository extends JpaRepository<Shipment, UUID> {
}
