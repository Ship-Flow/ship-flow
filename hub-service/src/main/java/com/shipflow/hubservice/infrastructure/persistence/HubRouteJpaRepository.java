package com.shipflow.hubservice.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shipflow.hubservice.domain.hub.HubRoute;

public interface HubRouteJpaRepository extends JpaRepository<HubRoute, UUID> {

	List<HubRoute> findAllByDeletedAtIsNull();

	Optional<HubRoute> findByDepartureHub_IdAndArrivalHub_IdAndDeletedAtIsNull(
		UUID departureHubId, UUID arrivalHubId);
}
