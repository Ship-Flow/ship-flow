package com.shipflow.orderservice.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyInfo {

    private UUID supplierCompanyId;
    private UUID receiverCompanyId;

    public CompanyInfo(UUID supplierCompanyId, UUID receiverCompanyId) {
        this.supplierCompanyId = supplierCompanyId;
        this.receiverCompanyId = receiverCompanyId;
    }
}
