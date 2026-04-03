package com.shipflow.shipmentservice.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shipflow.shipmentservice.domain.Shipment;

public interface ShipmentJpaRepository extends JpaRepository<Shipment, UUID> {

	@Query("""
			select distinct s from Shipment s
			left join fetch s.routes r
			left join fetch r.shipmentManager
			where s.id = :shipmentId
		""")
	Optional<Shipment> findByIdWithRoutes(UUID shipmentId);
}
