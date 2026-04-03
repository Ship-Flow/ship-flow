package com.shipflow.productservice.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, UUID> {
	Slice<ProductJpaEntity> findAllByCompanyId(UUID companyId, Pageable pageable);

	@Query("select stock from ProductJpaEntity where id = :productId and isHide = false")
	Integer findStockById(UUID productId);

	List<ProductJpaEntity> findAllByCompanyId(UUID companyId);
}
