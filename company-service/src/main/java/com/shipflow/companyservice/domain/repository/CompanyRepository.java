package com.shipflow.companyservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.shipflow.companyservice.domain.model.Company;

public interface CompanyRepository {
	Optional<Company> findById(UUID id);

	Company save(Company company);

	Slice<Company> findAll(Pageable pageable);

	Optional<Company> findByManagerId(UUID id);
}
