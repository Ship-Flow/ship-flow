package com.shipflow.productservice.presentation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.shipflow.productservice.domain.model.ProductStatus;

public record ProductUpdateResponse(
	String name, String description, BigDecimal price, Integer stock,
	ProductStatus productStatus, UUID companyId, String companyName,
	UUID hubId, Boolean isHide, LocalDateTime updateAt
) {
}
