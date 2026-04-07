package com.shipflow.orderservice.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull UUID productId,
        @Min(1) int quantity,
        @NotNull LocalDateTime requestDeadline,
        String requestNote
) {
}
