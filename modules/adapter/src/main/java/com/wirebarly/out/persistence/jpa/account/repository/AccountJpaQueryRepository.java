package com.wirebarly.out.persistence.jpa.account.repository;

import com.wirebarly.out.persistence.jpa.account.entity.AccountJpaEntity;

import java.util.Optional;

public interface AccountJpaQueryRepository {
    Optional<AccountJpaEntity> findOneActiveForUpdate(Long accountId);
}
