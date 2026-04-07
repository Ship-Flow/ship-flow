package com.shipflow.orderservice.domain.model;

import com.shipflow.orderservice.domain.exception.UnauthorizedException;

public enum UserRole {

    MASTER, HUB_MANAGER, SHIPMENT_MANAGER, COMPANY_MANAGER;

    public static UserRole from(String value) {
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException();
        }
    }

    /** 수정/삭제/취소 권한 (MASTER, HUB_MANAGER) */
    public boolean canManageOrder() {
        return this == MASTER || this == HUB_MANAGER;
    }

    /** 본인 주문만 조회 가능한 역할 (SHIPMENT_MANAGER, COMPANY_MANAGER) */
    public boolean isRestrictedToOwnOrders() {
        return this == COMPANY_MANAGER || this == SHIPMENT_MANAGER;
    }

    /** 수정/삭제/취소 권한 검증 — 권한 없으면 UnauthorizedException */
    public void requireManageOrder() {
        if (!canManageOrder()) throw new UnauthorizedException();
    }
}
