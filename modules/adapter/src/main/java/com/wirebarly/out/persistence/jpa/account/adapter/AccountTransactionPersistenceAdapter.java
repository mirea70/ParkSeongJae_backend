package com.wirebarly.out.persistence.jpa.account.adapter;

import com.wirebarly.account.model.AccountId;
import com.wirebarly.account.model.AccountTransaction;
import com.wirebarly.account.model.AccountTransactionType;
import com.wirebarly.out.PersistenceAdapter;
import com.wirebarly.out.account.AccountTransactionOutPort;
import com.wirebarly.out.persistence.jpa.account.entity.AccountTransactionJpaEntity;
import com.wirebarly.out.persistence.jpa.account.repository.AccountTransactionJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@PersistenceAdapter
@RequiredArgsConstructor
public class AccountTransactionPersistenceAdapter implements AccountTransactionOutPort {
    private final EntityManager entityManager;
    private final AccountTransactionJpaRepository accountTransactionJpaRepository;


    @Override
    public void insert(AccountTransaction accountTransaction) {
        entityManager.persist(AccountTransactionJpaEntity.from(accountTransaction));
    }

    @Override
    public Long getDailyWithdrawAmount(AccountId accountId, LocalDate today) {
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = from.plusDays(1);

        return accountTransactionJpaRepository.getSumByTypeBetween(accountId.getValue(), AccountTransactionType.WITHDRAW.name(), from, to);
    }
}
