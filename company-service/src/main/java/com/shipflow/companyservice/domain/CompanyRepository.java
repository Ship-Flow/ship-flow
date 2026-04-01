package com.shipflow.companyservice.domain;

import org.hibernate.validator.constraints.UUID;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository {
    Optional<Company> findById(UUID id);
    Company save(Company company);
    List<Company> findAll();
}
