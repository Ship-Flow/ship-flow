package com.shipflow.orderservice.domain.model;

public enum OrderStatus {
    CREATING("주문 요청이 들어오고 주문이 생성중인 상태입니다."),
    CREATED("유효성 검사 및 재고차감이 완료되어 주문이 생성된 상태입니다."),
    CANCELED("고객 요청 또는 주문자에의해, 주문이 취소된 상태입니다."),
    FAILED("배송 실패, 재고 부족 등으로 주문 생성이 실패한 상태입니다."),
    COMPLETED("배송이 완료되어 주문이 완료된 상태입니다.");

    private String description;

    private OrderStatus(String description){
        this.description = description;
    }
}
