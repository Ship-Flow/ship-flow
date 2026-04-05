package com.shipflow.productservice.presentation.dto.request;

import java.math.BigDecimal;

import com.shipflow.productservice.domain.model.ProductStatus;

public record ProductUpdateInfoRequest(
	String name, BigDecimal price, ProductStatus status
) {
}