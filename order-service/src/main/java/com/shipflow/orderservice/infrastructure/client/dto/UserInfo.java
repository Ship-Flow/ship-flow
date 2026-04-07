package com.shipflow.orderservice.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UserInfo(
        @JsonProperty("id") UUID userId,
        @JsonProperty("name") String ordererName,
        @JsonProperty("companyId") UUID receiverCompanyId
) {}
