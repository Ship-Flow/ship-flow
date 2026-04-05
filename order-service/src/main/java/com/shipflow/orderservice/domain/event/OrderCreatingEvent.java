package com.shipflow.orderservice.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatingEvent(
        UUID orderId,
        UUID ordererId,
        UUID productId,
        UUID supplierCompanyId,
        UUID receiverCompanyId,
        UUID departureHubId,
        UUID arrivalHubId,
        int quantity,
        LocalDateTime requestDeadline,
        String requestNote,
        UUID createdBy,
        LocalDateTime createdAt
) {}
