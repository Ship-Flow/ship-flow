package com.shipflow.hubservice.application.event;

import java.util.UUID;

public record HubManagerChangedEvent(
	UUID oldManagerId,
	UUID newManagerId,
	UUID hubId,
	UUID requestUserId
) {
}
