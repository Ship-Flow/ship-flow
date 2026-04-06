package com.shipflow.orderservice.infrastructure.client.dto;

import java.util.UUID;

public record ProductInfo(
        UUID productId,
        String productName,
        UUID supplierCompanyId,
        String supplierCompanyName,
        UUID departureHubId,
        Integer stock
) {}
