package com.shipflow.companyservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CompanyCreateResponse(UUID id, String name,
                                    String type, UUID hubId,
                                    String address, UUID managerId,
                                    String managerName, LocalDateTime createdAt) {
}
