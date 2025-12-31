package com.wirebarly.out.persistence.jpa.account.repository;

import com.wirebarly.out.persistence.jpa.account.entity.AccountTransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransactionJpaEntity, Long> {
}
