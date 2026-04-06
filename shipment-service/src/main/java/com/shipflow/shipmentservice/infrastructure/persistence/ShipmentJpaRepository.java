package com.shipflow.shipmentservice.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shipflow.shipmentservice.domain.Shipment;

public interface ShipmentJpaRepository extends JpaRepository<Shipment, UUID> {

	@Query("""
			select distinct s from Shipment s
			left join fetch s.shipmentManager
			where s.id = :shipmentId
			and s.deletedAt is null 
		""")
	Optional<Shipment> findByIdWithManager(@Param("shipmentId") UUID shipmentId);

	@Query("""
			select distinct s from Shipment s
			left join fetch s.routes r
			left join fetch r.shipmentManager
			where s.id = :shipmentId
			and s.deletedAt is null 
		""")
	Optional<Shipment> findByIdWithRoutesAndManager(@Param("shipmentId") UUID shipmentId);

	@Query("""
			select distinct s from Shipment s
			left join fetch s.routes r
			where s.orderId = :orderId
			and s.deletedAt is null 
		""")
	Optional<Shipment> findByOrderIdWithRoutes(@Param("orderId") UUID orderId);
}
