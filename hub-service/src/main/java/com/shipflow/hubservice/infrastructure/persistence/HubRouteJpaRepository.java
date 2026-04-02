package com.shipflow.hubservice.infrastructure.persistence;

import com.shipflow.hubservice.domain.hub.HubRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HubRouteJpaRepository extends JpaRepository<HubRoute, UUID> {
}
