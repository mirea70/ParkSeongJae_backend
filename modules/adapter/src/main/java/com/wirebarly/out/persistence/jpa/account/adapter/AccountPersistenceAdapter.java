package com.wirebarly.out.persistence.jpa.account.adapter;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.AccountId;
import com.wirebarly.account.model.AccountStatus;
import com.wirebarly.out.PersistenceAdapter;
import com.wirebarly.out.account.AccountOutPort;
import com.wirebarly.out.persistence.jpa.account.entity.AccountJpaEntity;
import com.wirebarly.out.persistence.jpa.account.repository.AccountJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@PersistenceAdapter
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountOutPort {
    private final AccountJpaRepository accountJpaRepository;
    private final EntityManager entityManager;

    @Override
    public Account insert(Account account) {
        entityManager.persist(AccountJpaEntity.from(account));
        return account;
    }

    @Override
    public Optional<Account> loadOne(AccountId id) {
        return accountJpaRepository.findByAccountIdAndStatusAndClosedAtIsNull(id.getValue(), AccountStatus.ACTIVE.name())
                .map(AccountJpaEntity::toDomain);
    }

    @Override
    public void update(Account account) {
        AccountJpaEntity accountJpaEntity = accountJpaRepository.getReferenceById(account.getId().getValue());
        accountJpaEntity.updateFrom(account);
    }
}
