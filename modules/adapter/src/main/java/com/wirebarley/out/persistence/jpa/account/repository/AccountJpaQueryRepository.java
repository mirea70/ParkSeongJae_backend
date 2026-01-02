package com.wirebarley.out.persistence.jpa.account.repository;

import com.wirebarley.out.persistence.jpa.account.entity.AccountJpaEntity;

import java.util.Optional;

public interface AccountJpaQueryRepository {
    Optional<AccountJpaEntity> findOneActiveForUpdate(Long accountId);
}
