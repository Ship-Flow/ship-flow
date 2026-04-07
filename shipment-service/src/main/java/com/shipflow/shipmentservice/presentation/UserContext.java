package com.shipflow.shipmentservice.presentation;

import java.util.UUID;

public record UserContext(UUID userId, String role) {
}