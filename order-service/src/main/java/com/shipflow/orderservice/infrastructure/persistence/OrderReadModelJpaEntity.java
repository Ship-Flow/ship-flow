package com.shipflow.orderservice.infrastructure.persistence;

import com.shipflow.orderservice.domain.model.OrderReadModel;
import com.shipflow.orderservice.domain.model.OrderStatus;
import com.shipflow.orderservice.domain.model.ShipmentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "p_order_read_models",
    indexes = {
        @Index(name = "idx_read_models_orderer_created", columnList = "orderer_id, created_at DESC")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderReadModelJpaEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(columnDefinition = "uuid")
    private UUID shipmentId;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus shipmentStatus;

    @Column(columnDefinition = "uuid")
    private UUID supplierCompanyId;
    private String supplierCompanyName;

    @Column(columnDefinition = "uuid")
    private UUID receiverCompanyId;
    private String receiverCompanyName;

    @Column(columnDefinition = "uuid")
    private UUID ordererId;
    private String ordererName;

    @Column(columnDefinition = "uuid")
    private UUID productId;
    private String productName;

    private int quantity;

    @Column(columnDefinition = "uuid")
    private UUID departureHubId;
    private String departureHubName;

    @Column(columnDefinition = "uuid")
    private UUID arrivalHubId;
    private String arrivalHubName;

    private LocalDateTime requestDeadline;
    private String requestNote;
    private String cancelReason;

    private LocalDateTime createdAt;
    @Column(columnDefinition = "uuid")
    private UUID createdBy;

    private LocalDateTime updatedAt;
    @Column(columnDefinition = "uuid")
    private UUID updatedBy;

    private LocalDateTime deletedAt;
    @Column(columnDefinition = "uuid")
    private UUID deletedBy;

    public static OrderReadModelJpaEntity from(OrderReadModel model) {
        OrderReadModelJpaEntity entity = new OrderReadModelJpaEntity();
        entity.orderId = model.getOrderId();
        entity.orderStatus = model.getOrderStatus();
        entity.shipmentId = model.getShipmentId();
        entity.shipmentStatus = model.getShipmentStatus();
        entity.supplierCompanyId = model.getSupplierCompanyId();
        entity.supplierCompanyName = model.getSupplierCompanyName();
        entity.receiverCompanyId = model.getReceiverCompanyId();
        entity.receiverCompanyName = model.getReceiverCompanyName();
        entity.ordererId = model.getOrdererId();
        entity.ordererName = model.getOrdererName();
        entity.productId = model.getProductId();
        entity.productName = model.getProductName();
        entity.quantity = model.getQuantity();
        entity.departureHubId = model.getDepartureHubId();
        entity.departureHubName = model.getDepartureHubName();
        entity.arrivalHubId = model.getArrivalHubId();
        entity.arrivalHubName = model.getArrivalHubName();
        entity.requestDeadline = model.getRequestDeadline();
        entity.requestNote = model.getRequestNote();
        entity.cancelReason = model.getCancelReason();
        entity.createdAt = model.getCreatedAt();
        entity.createdBy = model.getCreatedBy();
        entity.updatedAt = model.getUpdatedAt();
        entity.updatedBy = model.getUpdatedBy();
        entity.deletedAt = model.getDeletedAt();
        entity.deletedBy = model.getDeletedBy();
        return entity;
    }

    public OrderReadModel toDomain() {
        return OrderReadModel.builder()
                .orderId(orderId)
                .orderStatus(orderStatus)
                .shipmentId(shipmentId)
                .shipmentStatus(shipmentStatus)
                .supplierCompanyId(supplierCompanyId)
                .supplierCompanyName(supplierCompanyName)
                .receiverCompanyId(receiverCompanyId)
                .receiverCompanyName(receiverCompanyName)
                .ordererId(ordererId)
                .ordererName(ordererName)
                .productId(productId)
                .productName(productName)
                .quantity(quantity)
                .departureHubId(departureHubId)
                .departureHubName(departureHubName)
                .arrivalHubId(arrivalHubId)
                .arrivalHubName(arrivalHubName)
                .requestDeadline(requestDeadline)
                .requestNote(requestNote)
                .cancelReason(cancelReason)
                .createdAt(createdAt)
                .createdBy(createdBy)
                .updatedAt(updatedAt)
                .updatedBy(updatedBy)
                .deletedAt(deletedAt)
                .deletedBy(deletedBy)
                .build();
    }
}
