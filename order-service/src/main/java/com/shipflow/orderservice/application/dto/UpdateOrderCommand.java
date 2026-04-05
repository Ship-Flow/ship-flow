package com.shipflow.orderservice.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateOrderCommand(
        UUID productId,
        UUID supplierCompanyId,
        UUID receiverCompanyId,
        UUID departureHubId,
        UUID arrivalHubId,
        int quantity,
        LocalDateTime requestDeadline,
        String requestNote
) {
}
