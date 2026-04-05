package com.shipflow.orderservice.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderUpdatedEvent(
        UUID orderId,
        UUID productId,
        UUID supplierCompanyId,
        UUID receiverCompanyId,
        UUID departureHubId,
        UUID arrivalHubId,
        int quantity,
        LocalDateTime requestDeadline,
        String requestNote,
        UUID updatedBy,
        LocalDateTime updatedAt
) {}
