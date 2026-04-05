package com.shipflow.orderservice.presentation.dto;

import com.shipflow.orderservice.application.dto.OrderResult;
import com.shipflow.orderservice.domain.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID ordererId,
        UUID productId,
        UUID shipmentId,
        UUID supplierCompanyId,
        UUID receiverCompanyId,
        UUID departureHubId,
        UUID arrivalHubId,
        int quantity,
        OrderStatus status,
        String cancelReason,
        LocalDateTime requestDeadline,
        String requestNote,
        UUID createdBy,
        LocalDateTime createdAt,
        UUID updatedBy,
        LocalDateTime updatedAt
) {
    public static OrderResponse from(OrderResult result) {
        return new OrderResponse(
                result.id(),
                result.ordererId(),
                result.productId(),
                result.shipmentId(),
                result.supplierCompanyId(),
                result.receiverCompanyId(),
                result.departureHubId(),
                result.arrivalHubId(),
                result.quantity(),
                result.status(),
                result.cancelReason(),
                result.requestDeadline(),
                result.requestNote(),
                result.createdBy(),
                result.createdAt(),
                result.updatedBy(),
                result.updatedAt()
        );
    }
}
