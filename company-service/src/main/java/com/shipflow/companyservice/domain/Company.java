package com.shipflow.companyservice.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import lombok.Getter;

@Getter
public class Company extends BaseEntity {
	private UUID id;
	private String name;
	private CompanyType type;
	private UUID hubId;
	private String address;
	private UUID managerId;
	private String managerName;

	private Company(String name, CompanyType type, UUID hubId,
		String address, UUID managerId, String managerName) {
		this.name = Objects.requireNonNull(name, "name은 필수값입니다.");
		this.type = Objects.requireNonNull(type, "type은 필수값입니다.");
		this.hubId = Objects.requireNonNull(hubId, "hubId는 필수값입니다.");
		this.address = Objects.requireNonNull(address, "address는 필수값입니다.");
		this.managerId = Objects.requireNonNull(managerId, "managerId는 필수값입니다.");
		this.managerName = Objects.requireNonNull(managerName, "managerName은 필수값입니다.");
	}

	public static Company create(String name, CompanyType type, UUID hubId,
		String address, UUID managerId, String managerName, UUID createdBy) {
		Company company = new Company(name, type, hubId, address, managerId, managerName);
		company.create(createdBy);
		return company;
	}

	public static Company reconstruct(UUID id, String name, CompanyType type, UUID hubId, String address,
		UUID managerId, String managerName, LocalDateTime createdAt, UUID createdBy,
		LocalDateTime updatedAt, UUID updatedBy, LocalDateTime deletedAt, UUID deletedBy) {
		Company company = new Company(name, type, hubId, address, managerId, managerName);
		company.id = id;
		company.createdAt = createdAt;
		company.createdBy = createdBy;
		company.updatedAt = updatedAt;
		company.updatedBy = updatedBy;
		company.deletedAt = deletedAt;
		company.deletedBy = deletedBy;
		return company;
	}

	public void updateByAdmin(String name, CompanyType type, UUID hubId,
		String address, UUID managerId, String managerName, UUID updatedBy) {
		if (name != null && !name.isBlank() || address != null && !address.isBlank())
			updateBasic(name, address);
		if (type != null)
			this.type = type;
		if (hubId != null)
			this.hubId = hubId;
		if (managerId != null && managerName != null && !managerName.isBlank()) {
			this.managerName = managerName;
			this.managerId = managerId;
		}
		this.update(updatedBy);
	}

	public void updateByCompany(String name, String address, UUID updatedBy) {
		updateBasic(name, address);
		this.update(updatedBy);
	}

	public void updateBasic(String name, String address) {
		if (name != null && !name.isBlank())
			this.name = name;
		if (address != null && !address.isBlank())
			this.address = address;
	}

	public void delete(UUID deletedBy) {
		super.delete(deletedBy);
	}
}
