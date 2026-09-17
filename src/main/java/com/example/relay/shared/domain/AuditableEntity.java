package com.example.relay.shared.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

@MappedSuperclass
public abstract class AuditableEntity extends BaseEntity{
    @Getter
    @CreatedBy
    private String createdBy;

    @Getter
    @LastModifiedBy
    private String lastModifiedBy;
}
