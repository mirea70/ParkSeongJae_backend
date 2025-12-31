package com.wirebarly.out.persistence.jpa.account.adapter;

import com.wirebarly.account.model.AccountTransaction;
import com.wirebarly.out.PersistenceAdapter;
import com.wirebarly.out.account.AccountTransactionOutPort;
import com.wirebarly.out.persistence.jpa.account.entity.AccountTransactionJpaEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class AccountTransactionPersistenceAdapter implements AccountTransactionOutPort {
    private final EntityManager entityManager;


    @Override
    public void insert(AccountTransaction accountTransaction) {
        entityManager.persist(AccountTransactionJpaEntity.from(accountTransaction));
    }
}
