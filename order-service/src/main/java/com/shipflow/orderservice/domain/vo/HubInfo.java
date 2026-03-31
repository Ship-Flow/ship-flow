package com.shipflow.orderservice.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubInfo {

    private UUID departureHubId;
    private UUID arrivalHubId;

    public HubInfo(UUID departureHubId, UUID arrivalHubId) {
        this.departureHubId = departureHubId;
        this.arrivalHubId = arrivalHubId;
    }
}
