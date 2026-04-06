package com.shipflow.userservice.presentation.internal.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PatchInternalUserReqDto {

	private UUID hubId;
	private UUID companyId;
	private UUID updateBy;
	private String updatedAt;

	@JsonIgnore
	private boolean hubIdUpdated;

	@JsonIgnore
	private boolean companyIdUpdated;

	public void setHubId(UUID hubId) {
		this.hubId = hubId;
		this.hubIdUpdated = true;
	}

	public void setCompanyId(UUID companyId) {
		this.companyId = companyId;
		this.companyIdUpdated = true;
	}

	public void setUpdateBy(UUID updateBy) {
		this.updateBy = updateBy;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
}