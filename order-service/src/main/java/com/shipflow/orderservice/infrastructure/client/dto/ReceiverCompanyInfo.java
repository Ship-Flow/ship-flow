package com.shipflow.orderservice.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ReceiverCompanyInfo(
        @JsonProperty("receiverCompanyId") UUID companyId,
        @JsonProperty("receiverCompanyName") String companyName,
        @JsonProperty("departureCompanyHubId") UUID hubId,
        String address
) {}
