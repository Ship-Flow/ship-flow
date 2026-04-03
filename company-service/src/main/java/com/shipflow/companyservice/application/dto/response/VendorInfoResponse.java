package com.shipflow.companyservice.application.dto.response;

import java.util.UUID;

import lombok.NonNull;

public record VendorInfoResponse(
	@NonNull UUID id
) {
}
