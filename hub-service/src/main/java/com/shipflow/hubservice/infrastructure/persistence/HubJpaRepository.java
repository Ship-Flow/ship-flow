package com.shipflow.hubservice.infrastructure.persistence;

import com.shipflow.hubservice.domain.hub.Hub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HubJpaRepository extends JpaRepository<Hub, UUID> {
}
