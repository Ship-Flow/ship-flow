package com.shipflow.orderservice.application.dto;

import com.shipflow.orderservice.domain.model.Order;
import com.shipflow.orderservice.domain.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResult(
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
        String deliveryAddress,
        UUID createdBy,
        LocalDateTime createdAt,
        UUID updatedBy,
        LocalDateTime updatedAt
) {
    public static OrderResult from(Order order) {
        return new OrderResult(
                order.getId(),
                order.getOrdererId(),
                order.getProductId(),
                order.getShipmentId(),
                order.getCompanyInfo().getSupplierCompanyId(),
                order.getCompanyInfo().getReceiverCompanyId(),
                order.getHubInfo().getDepartureHubId(),
                order.getHubInfo().getArrivalHubId(),
                order.getQuantity().getValue(),
                order.getStatus(),
                order.getCancelReason(),
                order.getRequestDeadline(),
                order.getRequestNote(),
                order.getDeliveryAddress(),
                order.getCreatedBy(),
                order.getCreatedAt(),
                order.getUpdatedBy(),
                order.getUpdatedAt()
        );
    }
}
