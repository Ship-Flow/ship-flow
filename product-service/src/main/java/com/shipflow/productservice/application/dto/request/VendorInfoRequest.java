package com.shipflow.productservice.application.dto.request;

import java.util.UUID;

import lombok.NonNull;

public record VendorInfoRequest(
	@NonNull UUID id
) {
}
