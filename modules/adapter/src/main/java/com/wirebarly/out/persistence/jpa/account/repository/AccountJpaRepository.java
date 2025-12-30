package com.wirebarly.out.persistence.jpa.account.repository;

import com.wirebarly.out.persistence.jpa.account.entity.AccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, Long> {

    /**
     *
     * Hibernate:
     *     SELECT *
     *     FROM account
     *     WHERE account_id=?
     *     AND status=?
     *     AND closed_at is null
     */
    Optional<AccountJpaEntity> findByAccountIdAndStatusAndClosedAtIsNull(Long accountId, String status);
}
