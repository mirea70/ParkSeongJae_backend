package com.wirebarley.out.persistence.jpa.account.repository;

import com.wirebarley.out.persistence.jpa.account.entity.AccountTransactionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountTransactionJpaRepository extends JpaRepository<AccountTransactionJpaEntity, Long>, AccountTransactionJpaQueryRepository {
    List<AccountTransactionJpaEntity> findAllByAccountId(Long accountId);
}
