package com.wirebarly.out.persistence.jpa.common;

import java.time.LocalDateTime;

public abstract class BaseJpaEntity {
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}
