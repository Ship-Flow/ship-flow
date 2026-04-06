package com.shipflow.companyservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyUpdateResponse(UUID id, String name,
									String type, UUID hubId,
									String address, UUID managerId,
									String managerName, LocalDateTime updatedAt) {
}
