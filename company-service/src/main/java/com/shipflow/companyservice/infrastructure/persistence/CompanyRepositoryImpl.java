package com.shipflow.companyservice.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.shipflow.companyservice.domain.model.Company;
import com.shipflow.companyservice.domain.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepository {
	private final CompanyJpaRepository jpaRepository;

	@Override
	public Optional<Company> findById(UUID id) {
		return jpaRepository.findById(id)
			.map(CompanyJpaEntity::toDomain);
	}

	@Override
	public Company save(Company company) {
		CompanyJpaEntity jpaEntity = CompanyJpaEntity.from(company);
		CompanyJpaEntity savedCompany = jpaRepository.save(jpaEntity);
		return savedCompany.toDomain();
	}

	@Override
	public Slice<Company> findAll(Pageable pageable) {
		Slice<CompanyJpaEntity> jpaEntities = jpaRepository.findAll(pageable);
		return jpaEntities.map(CompanyJpaEntity::toDomain);
	}

	@Override
	public Optional<Company> findByManagerId(UUID id) {
		return jpaRepository.findByManagerId(id)
			.map(CompanyJpaEntity::toDomain);
	}

	@Override
	public List<Company> findAllByHubId(UUID hubId) {
		return jpaRepository.findAllByHubId(hubId)
			.stream()
			.map(CompanyJpaEntity::toDomain)
			.collect(Collectors.toList());
	}
}
