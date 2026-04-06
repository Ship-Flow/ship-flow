package com.shipflow.orderservice.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubInfo {

    private UUID departureHubId;
    private UUID arrivalHubId;

    public HubInfo(UUID departureHubId, UUID arrivalHubId) {
        Objects.requireNonNull(departureHubId, "departureHubId는 필수입니다.");
        Objects.requireNonNull(arrivalHubId, "arrivalHubId는 필수입니다.");
        this.departureHubId = departureHubId;
        this.arrivalHubId = arrivalHubId;
    }
}
