package com.shipflow.orderservice.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quantity {

    private int value;

    public Quantity(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다");
        }
        this.value = value;
    }
}
