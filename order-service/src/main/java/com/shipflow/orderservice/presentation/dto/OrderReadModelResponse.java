package com.shipflow.orderservice.presentation.dto;

import com.shipflow.orderservice.domain.model.OrderReadModel;
import com.shipflow.orderservice.domain.model.OrderStatus;
import com.shipflow.orderservice.domain.model.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderReadModelResponse(
        UUID orderId,
        OrderStatus orderStatus,
        UUID ordererId,
        String ordererName,
        UUID productId,
        String productName,
        int quantity,
        UUID supplierCompanyId,
        String supplierCompanyName,
        UUID receiverCompanyId,
        String receiverCompanyName,
        UUID shipmentId,
        ShipmentStatus shipmentStatus,
        UUID departureHubId,
        String departureHubName,
        UUID arrivalHubId,
        String arrivalHubName,
        LocalDateTime requestDeadline,
        String requestNote,
        String cancelReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static OrderReadModelResponse from(OrderReadModel model) {
        return new OrderReadModelResponse(
                model.getOrderId(),
                model.getOrderStatus(),
                model.getOrdererId(),
                model.getOrdererName(),
                model.getProductId(),
                model.getProductName(),
                model.getQuantity(),
                model.getSupplierCompanyId(),
                model.getSupplierCompanyName(),
                model.getReceiverCompanyId(),
                model.getReceiverCompanyName(),
                model.getShipmentId(),
                model.getShipmentStatus(),
                model.getDepartureHubId(),
                model.getDepartureHubName(),
                model.getArrivalHubId(),
                model.getArrivalHubName(),
                model.getRequestDeadline(),
                model.getRequestNote(),
                model.getCancelReason(),
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}
