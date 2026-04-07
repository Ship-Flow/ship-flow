package com.shipflow.productservice.application.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NonNull;

public record VendorInfoResponse(
	@JsonProperty("receiverCompanyId") @NonNull UUID id,
	@JsonProperty("receiverCompanyName") String name,
	@JsonProperty("departureCompanyHubId") @NonNull UUID hubId
) {
}
