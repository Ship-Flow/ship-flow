package com.shipflow.companyservice.fixture;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.test.util.ReflectionTestUtils;

import com.shipflow.companyservice.domain.model.Company;
import com.shipflow.companyservice.domain.model.CompanyType;
import com.shipflow.companyservice.infrastructure.persistence.CompanyJpaEntity;

public class CompanyFixture {
	public static Company create() {
		CompanyJpaEntity company = new CompanyJpaEntity();
		ReflectionTestUtils.setField(company, "id", UUID.randomUUID());
		ReflectionTestUtils.setField(company, "name", "testName");
		ReflectionTestUtils.setField(company, "type", CompanyType.Receiver);
		ReflectionTestUtils.setField(company, "hubId", UUID.randomUUID());
		ReflectionTestUtils.setField(company, "address", "testAddress");
		ReflectionTestUtils.setField(company, "managerId", UUID.randomUUID());
		ReflectionTestUtils.setField(company, "managerName", "testManagerName");
		ReflectionTestUtils.setField(company, "createdAt", LocalDateTime.now());
		ReflectionTestUtils.setField(company, "createdBy", UUID.randomUUID());
		return company.toDomain();
	}
}
