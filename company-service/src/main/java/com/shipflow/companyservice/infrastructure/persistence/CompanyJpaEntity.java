package com.shipflow.companyservice.infrastructure.persistence;

import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;

import com.shipflow.common.domain.BaseEntity;
import com.shipflow.companyservice.domain.model.Company;
import com.shipflow.companyservice.domain.model.CompanyType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor()
@SQLRestriction("deleted_at IS NULL")
@Table(name = "p_company")
public class CompanyJpaEntity extends BaseEntity {
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

	//domain->jpa_entity
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

	//jpa_entity->domain
	public Company toDomain() {
		return Company.reconstruct(id, name, type,
			hubId, address, managerId, managerName,
			createdAt, createdBy, updatedAt, updatedBy,
			deletedAt, deletedBy);
	}
}
