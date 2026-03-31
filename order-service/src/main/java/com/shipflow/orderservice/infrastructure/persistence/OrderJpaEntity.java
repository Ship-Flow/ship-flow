package com.shipflow.orderservice.infrastructure.persistence;

import com.shipflow.orderservice.domain.model.Order;
import com.shipflow.orderservice.domain.model.OrderStatus;
import com.shipflow.orderservice.domain.vo.CompanyInfo;
import com.shipflow.orderservice.domain.vo.HubInfo;
import com.shipflow.orderservice.domain.vo.Quantity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_orders")
@SQLDelete(sql = "UPDATE p_orders SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderJpaEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID ordererId;

    @Column(nullable = false, columnDefinition = "uuid")
    private UUID productId;

    @Column(columnDefinition = "uuid")
    private UUID shipmentId;

    @Embedded
    private CompanyInfo companyInfo;

    @Embedded
    private HubInfo hubInfo;

    @Embedded
    private Quantity quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private String cancelReason;

    private LocalDateTime requestDeadline;

    private String requestNote;

    @Column(updatable = false, columnDefinition = "uuid")
    private UUID createdBy;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "uuid")
    private UUID updatedBy;

    private LocalDateTime updatedAt;

    @Column(columnDefinition = "uuid")
    private UUID deletedBy;

    private LocalDateTime deletedAt;

    public static OrderJpaEntity from(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.id = order.getId();
        entity.ordererId = order.getOrdererId();
        entity.productId = order.getProductId();
        entity.shipmentId = order.getShipmentId();
        entity.companyInfo = order.getCompanyInfo();
        entity.hubInfo = order.getHubInfo();
        entity.quantity = order.getQuantity();
        entity.status = order.getStatus();
        entity.cancelReason = order.getCancelReason();
        entity.requestDeadline = order.getRequestDeadline();
        entity.requestNote = order.getRequestNote();
        entity.createdBy = order.getCreatedBy();
        entity.createdAt = order.getCreatedAt();
        entity.updatedBy = order.getUpdatedBy();
        entity.updatedAt = order.getUpdatedAt();
        entity.deletedBy = order.getDeletedBy();
        entity.deletedAt = order.getDeletedAt();
        return entity;
    }

    public Order toDomain() {
        return Order.reconstruct(
                id, ordererId, productId, shipmentId,
                companyInfo, hubInfo, quantity,
                status, cancelReason, requestDeadline, requestNote,
                createdBy, createdAt, updatedBy, updatedAt, deletedBy, deletedAt
        );
    }
}
