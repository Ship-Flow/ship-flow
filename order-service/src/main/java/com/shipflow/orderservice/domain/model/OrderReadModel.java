package com.shipflow.orderservice.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class OrderReadModel {

    private UUID orderId;
    private OrderStatus orderStatus;
    private UUID shipmentId;
    private String shipmentStatus;
    private UUID supplierCompanyId;
    private String supplierCompanyName;
    private UUID receiverCompanyId;
    private String receiverCompanyName;
    private UUID ordererId;
    private String ordererName;
    private UUID productId;
    private String productName;
    private int quantity;
    private UUID departureHubId;
    private String departureHubName;
    private UUID arrivalHubId;
    private String arrivalHubName;
    private LocalDateTime requestDeadline;
    private String requestNote;
    private String cancelReason;
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
    private LocalDateTime deletedAt;
    private UUID deletedBy;
}
