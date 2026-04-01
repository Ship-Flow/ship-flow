package com.shipflow.companyservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.shipflow.companyservice.domain.Company;

public interface CompanyRepository {
	Optional<Company> findById(UUID id);

	Company save(Company company);

	List<Company> findAll();

	Optional<Company> findByManagerId(UUID id);
}
