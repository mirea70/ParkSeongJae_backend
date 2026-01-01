package com.wirebarly.out.persistence.jpa.transfer.repository;

import java.time.LocalDateTime;

public interface TransferJpaQueryRepository {
    Long getSumByFromAccountBetween(Long fromAccountId, LocalDateTime from, LocalDateTime to);
}
