package com.wirebarley.out.persistence.jpa.account.adapter;

import com.wirebarley.account.model.AccountId;
import com.wirebarley.account.model.AccountTransaction;
import com.wirebarley.account.model.AccountTransactionType;
import com.wirebarley.out.PersistenceAdapter;
import com.wirebarley.out.account.AccountTransactionOutPort;
import com.wirebarley.out.persistence.jpa.account.entity.AccountTransactionJpaEntity;
import com.wirebarley.out.persistence.jpa.account.repository.AccountTransactionJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    public void insert(List<AccountTransaction> accountTransactions) {
        for(AccountTransaction accountTransaction : accountTransactions){
            insert(accountTransaction);
        }
    }

    @Override
    public Long getDailyWithdrawAmount(AccountId accountId, LocalDate today) {
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = from.plusDays(1);

        return accountTransactionJpaRepository.getSumByTypeBetween(accountId.getValue(), AccountTransactionType.WITHDRAW.name(), from, to);
    }
}
