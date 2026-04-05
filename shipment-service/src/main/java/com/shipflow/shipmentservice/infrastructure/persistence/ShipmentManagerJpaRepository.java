package com.shipflow.shipmentservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shipflow.shipmentservice.domain.ShipmentManager;
import com.shipflow.shipmentservice.domain.ShipmentManagerType;

public interface ShipmentManagerJpaRepository extends JpaRepository<ShipmentManager, UUID> {

	@Query("""
			select coalesce(max(sm.shipmentSequence), 0)
			  from ShipmentManager sm
			 where sm.type = :type
			   and sm.deletedAt is null
		""")
	int findMaxSequenceByType(@Param("type") ShipmentManagerType type);

	@Query("""
			select coalesce(max(sm.shipmentSequence), 0)
			  from ShipmentManager sm
			 where sm.type = :type
			   and sm.hubId = :hubId
			   and sm.deletedAt is null
		""")
	int findMaxSequenceByTypeAndHubId(
		@Param("type") ShipmentManagerType type,
		@Param("hubId") UUID hubId
	);
}
