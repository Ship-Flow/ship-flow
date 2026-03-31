package com.shipflow.orderservice.domain.model;

import com.shipflow.orderservice.domain.exception.InvalidOrderStateException;
import com.shipflow.orderservice.domain.vo.CompanyInfo;
import com.shipflow.orderservice.domain.vo.HubInfo;
import com.shipflow.orderservice.domain.vo.Quantity;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Order {

    private UUID id;
    private UUID ordererId;
    private UUID productId;
    private UUID shipmentId;
    private CompanyInfo companyInfo;
    private HubInfo hubInfo;
    private Quantity quantity;
    private OrderStatus status;
    private String cancelReason;
    private LocalDateTime requestDeadline;
    private String requestNote;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private UUID updatedBy;
    private LocalDateTime updatedAt;
    private UUID deletedBy;
    private LocalDateTime deletedAt;

    protected Order() {
    }

    public static Order create(
            UUID ordererId,
            UUID productId,
            CompanyInfo companyInfo,
            HubInfo hubInfo,
            Quantity quantity,
            LocalDateTime requestDeadline,
            String requestNote,
            UUID createdBy
    ) {
        Order order = new Order();
        order.id = UUID.randomUUID();
        order.ordererId = ordererId;
        order.productId = productId;
        order.companyInfo = companyInfo;
        order.hubInfo = hubInfo;
        order.quantity = quantity;
        order.requestDeadline = requestDeadline;
        order.requestNote = requestNote;
        order.createdBy = createdBy;
        order.status = OrderStatus.CREATING;
        order.createdAt = LocalDateTime.now();
        return order;
    }

    public void confirmCreation() {
        if (this.status != OrderStatus.CREATING) {
            throw new InvalidOrderStateException(
                    "confirmCreation은 CREATING 상태에서만 가능합니다. 현재 상태: " + this.status);
        }
        this.status = OrderStatus.CREATED;
    }

    public void fail() {
        if (this.status != OrderStatus.CREATING && this.status != OrderStatus.CREATED) {
            throw new InvalidOrderStateException(
                    "현재 상태 " + this.status + "에서는 실패 처리가 불가합니다.");
        }
        this.status = OrderStatus.FAILED;
    }

    public void cancel(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("취소 사유는 필수입니다.");
        }
        if (this.status != OrderStatus.CREATING && this.status != OrderStatus.CREATED) {
            throw new InvalidOrderStateException(
                    "cancel은 CREATING 또는 CREATED 상태에서만 가능합니다. 현재 상태: " + this.status);
        }
        this.status = OrderStatus.CANCELED;
        this.cancelReason = reason;
    }

    public void complete() {
        if (this.status != OrderStatus.CREATED) {
            throw new InvalidOrderStateException(
                    "complete는 CREATED 상태에서만 가능합니다. 현재 상태: " + this.status);
        }
        this.status = OrderStatus.COMPLETED;
    }

    public void linkShipment(UUID shipmentId) {
        if (this.status != OrderStatus.CREATED) {
            throw new InvalidOrderStateException(
                    "linkShipment는 CREATED 상태에서만 가능합니다. 현재 상태: " + this.status);
        }
        if (this.shipmentId != null) {
            throw new InvalidOrderStateException("이미 배송과 연결된 주문입니다.");
        }
        this.shipmentId = shipmentId;
    }

    public void softDelete(UUID deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void update(
            UUID productId,
            CompanyInfo companyInfo,
            HubInfo hubInfo,
            Quantity quantity,
            LocalDateTime requestDeadline,
            String requestNote,
            UUID updatedBy
    ) {
        if (this.status != OrderStatus.CREATING) {
            throw new InvalidOrderStateException(
                    "update는 CREATING 상태에서만 가능합니다. 현재 상태: " + this.status);
        }
        this.productId = productId;
        this.companyInfo = companyInfo;
        this.hubInfo = hubInfo;
        this.quantity = quantity;
        this.requestDeadline = requestDeadline;
        this.requestNote = requestNote;
        this.updatedBy = updatedBy;
        this.updatedAt = LocalDateTime.now();
    }
}
