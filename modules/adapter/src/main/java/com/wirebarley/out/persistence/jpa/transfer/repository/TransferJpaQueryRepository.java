package com.wirebarley.out.persistence.jpa.transfer.repository;

import com.wirebarley.in.transfer.result.TransferResult;

import java.time.LocalDateTime;
import java.util.List;

public interface TransferJpaQueryRepository {
    Long getSumByFromAccountBetween(Long fromAccountId, LocalDateTime from, LocalDateTime to);
    List<TransferResult> findAllByAccountId(Long accountId);
}
