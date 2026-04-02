package com.shipflow.orderservice.infrastructure.messaging.event.consume;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shipflow.common.messaging.event.SagaEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipmentCreationFailedEvent extends SagaEvent {

    private UUID orderId;
}
