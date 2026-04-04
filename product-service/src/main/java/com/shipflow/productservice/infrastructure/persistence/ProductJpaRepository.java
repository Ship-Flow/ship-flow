package com.shipflow.productservice.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID> {
	Slice<ProductJpaEntity> findAllByCompanyId(UUID companyId, Pageable pageable);
}
