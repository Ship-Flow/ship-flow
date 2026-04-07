package com.shipflow.orderservice.infrastructure.persistence;

import com.shipflow.common.domain.BaseEntity;
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
@SQLDelete(sql = "UPDATE p_orders SET deleted_at = NOW() WHERE id = ?")  // 논리삭제
@SQLRestriction("deleted_at IS NULL") // 모든 Query 문을 실행 할 때 delete_at 이 NULL 인 것만 가져오도록
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderJpaEntity extends BaseEntity {  // 데이터 모델

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

    private String deliveryAddress;

    // Order -> OrderJpaEntity (Order 객체를 DB 에 저장할때)
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
        entity.deliveryAddress = order.getDeliveryAddress();
        entity.createdBy = order.getCreatedBy();
        entity.createdAt = order.getCreatedAt();
        entity.updatedBy = order.getUpdatedBy();
        entity.updatedAt = order.getUpdatedAt();
        entity.deletedBy = order.getDeletedBy();
        entity.deletedAt = order.getDeletedAt();
        return entity;
    }

    // OrderJpaEntity -> Order (비즈니스 로직을 실행하기 위해 Order 객체로 변환할때 사용)
    public Order toDomain() {
        return Order.reconstruct(
                id, ordererId, productId, shipmentId,
                companyInfo, hubInfo, quantity,
                status, cancelReason, requestDeadline, requestNote, deliveryAddress,
                createdBy, createdAt, updatedBy, updatedAt, deletedBy, deletedAt
        );
    }
}
