package com.wirebarly.out.persistence.jpa.account.repository;

import java.time.LocalDateTime;

public interface AccountTransactionJpaQueryRepository {
    Long getSumByTypeBetween(Long accountId, String type, LocalDateTime from, LocalDateTime to);
}
