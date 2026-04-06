package com.shipflow.companyservice.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyJpaRepository extends JpaRepository<CompanyJpaEntity, UUID> {
	Optional<CompanyJpaEntity> findByManagerId(UUID id);

	List<CompanyJpaEntity> findAllByHubId(UUID hubId);
}
