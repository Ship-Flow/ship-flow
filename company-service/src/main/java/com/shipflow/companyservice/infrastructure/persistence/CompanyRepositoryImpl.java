package com.shipflow.companyservice.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.shipflow.companyservice.domain.Company;
import com.shipflow.companyservice.domain.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepository {
	private final CompanyJpaRepository jpaRepository;

	@Override
	public Optional<Company> findById(UUID id) {
		CompanyJpaEntity jpaEntity = jpaRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 업체를 찾을 수 없습니다."));
		Company company = jpaEntity.toDomain();
		return Optional.of(company);
	}

	@Override
	public void save(Company company) {
		CompanyJpaEntity jpaEntity = CompanyJpaEntity.from(company);
		jpaRepository.save(jpaEntity);
	}

	@Override
	public Slice<Company> findAll(Pageable pageable) {
		Slice<CompanyJpaEntity> jpaEntities = jpaRepository.findAll(pageable);
		return jpaEntities.map(CompanyJpaEntity::toDomain);
	}

	@Override
	public Optional<Company> findByManagerId(UUID id) {
		CompanyJpaEntity entity = jpaRepository.findByManagerId(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 업체를 찾을 수 없습니다."));
		return Optional.of(entity.toDomain());
	}
}
