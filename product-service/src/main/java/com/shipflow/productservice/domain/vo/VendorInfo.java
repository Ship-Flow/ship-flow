package com.shipflow.productservice.domain.vo;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VendorInfo {
	private UUID companyId;
	private String companyName;
	private UUID hubId;

	public VendorInfo(
		UUID companyId, String companyName, UUID hubId) {
		this.companyId = Objects.requireNonNull(companyId, "ComapnyId는 필수값입니다.");
		this.companyName = companyName;
		this.hubId = hubId;
	}
}
