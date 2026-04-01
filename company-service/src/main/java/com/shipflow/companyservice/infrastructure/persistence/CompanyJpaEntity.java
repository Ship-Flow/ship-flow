package com.shipflow.companyservice.infrastructure.persistence;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.companyservice.domain.Company;
import com.shipflow.companyservice.domain.CompanyType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_company")
public class CompanyJpaEntity {
	@Id
	@Column(columnDefinition = "uuid")
	private UUID id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private CompanyType type;

	@Column(columnDefinition = "uuid")
	private UUID hubId;

	@Column(nullable = false)
	private String address;

	@Column(columnDefinition = "uuid")
	private UUID managerId;

	private String managerName;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(columnDefinition = "uuid")
	private UUID createdBy;

	private LocalDateTime updatedAt;

	@Column(columnDefinition = "uuid")
	private UUID updatedBy;

	private LocalDateTime deletedAt;

	@Column(columnDefinition = "uuid")
	private UUID deletedBy;

	public static CompanyJpaEntity from(Company company) {
		CompanyJpaEntity entity = new CompanyJpaEntity();
		entity.id = company.getId();
		entity.name = company.getName();
		entity.type = company.getType();
		entity.hubId = company.getHubId();
		entity.address = company.getAddress();
		entity.managerId = company.getManagerId();
		entity.managerName = company.getManagerName();
		entity.createdAt = company.getCreatedAt();
		entity.createdBy = company.getCreatedBy();
		entity.updatedAt = company.getUpdatedAt();
		entity.updatedBy = company.getUpdatedBy();
		entity.deletedAt = company.getDeletedAt();
		entity.deletedBy = company.getDeletedBy();
		return entity;
	}

	public Company toDomain() {
		return Company.reconstruct(id, name, type,
			hubId, address, managerId, managerName,
			createdAt, createdBy, updatedAt, updatedBy,
			deletedAt, deletedBy);
	}
}
