package com.shipflow.orderservice.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatingEvent(
        UUID orderId,
        UUID ordererId,
        String ordererName,
        UUID productId,
        String productName,
        UUID supplierCompanyId,
        String supplierCompanyName,
        UUID receiverCompanyId,
        String receiverCompanyName,
        UUID departureHubId,
        UUID arrivalHubId,
        int quantity,
        LocalDateTime requestDeadline,
        String requestNote,
        UUID createdBy,
        LocalDateTime createdAt
) {}
