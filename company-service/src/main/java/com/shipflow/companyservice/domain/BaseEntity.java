package com.shipflow.companyservice.domain;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class BaseEntity {
    private LocalDateTime createdAt;
    private UUID createdBy;
    private LocalDateTime updatedAt;
    private UUID updatedBy;
    private LocalDateTime deletedAt;
    private UUID deletedBy;

    public void create(UUID id){
        this.createdAt = LocalDateTime.now();
        this.createdBy = id;
    }
    public void update(UUID id){
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = id;
    }
    public void delete(UUID id){
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = id;
    }
}
