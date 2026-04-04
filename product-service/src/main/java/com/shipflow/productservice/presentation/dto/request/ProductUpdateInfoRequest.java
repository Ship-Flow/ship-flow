package com.shipflow.productservice.presentation.dto.request;

import java.math.BigDecimal;

public record ProductUpdateInfoRequest(
	String name, BigDecimal price
) {
}