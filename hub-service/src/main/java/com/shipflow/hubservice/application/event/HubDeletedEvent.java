package com.shipflow.hubservice.application.event;

import java.util.UUID;

public record HubDeletedEvent(
	UUID hubId,
	UUID managerId,
	UUID requestUserId
) {
}
