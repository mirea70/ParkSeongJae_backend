package com.wirebarly.out.persistence.jpa.common;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseJpaEntity {
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
