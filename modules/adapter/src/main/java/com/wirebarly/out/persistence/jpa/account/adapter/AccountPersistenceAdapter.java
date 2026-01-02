package com.wirebarly.out.persistence.jpa.account.adapter;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.AccountId;
import com.wirebarly.account.model.AccountStatus;
import com.wirebarly.common.model.Loaded;
import com.wirebarly.out.PersistenceAdapter;
import com.wirebarly.out.account.AccountOutPort;
import com.wirebarly.out.persistence.jpa.account.entity.AccountJpaEntity;
import com.wirebarly.out.persistence.jpa.account.repository.AccountJpaRepository;
import com.wirebarly.out.persistence.jpa.common.JpaLoaded;
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
        entityManager.flush();
        return account;
    }

    @Override
    public Optional<Loaded<Account>> loadOne(AccountId id) {
        return accountJpaRepository.findByAccountIdAndStatusAndClosedAtIsNull(id.getValue(), AccountStatus.ACTIVE.name())
                .map(entity -> new JpaLoaded<>(entity.toDomain(), entity));
    }

    @Override
    public Optional<Loaded<Account>> loadOneForUpdate(AccountId id) {
        return accountJpaRepository.findOneActiveForUpdate(id.getValue())
                .map(entity -> new JpaLoaded<>(entity.toDomain(), entity));
    }

    @Override
    public void applyClose(Loaded<Account> loadedAccount) {
        JpaLoaded<Account, AccountJpaEntity> loaded = (JpaLoaded<Account, AccountJpaEntity>) loadedAccount;
        Account domain = loaded.domain();
        AccountJpaEntity entity = loaded.entity();

        entity.applyCloseFrom(domain);
    }

    @Override
    public void applyBalance(Loaded<Account> loadedAccount) {
        JpaLoaded<Account, AccountJpaEntity> loaded = (JpaLoaded<Account, AccountJpaEntity>) loadedAccount;
        Account domain = loaded.domain();
        AccountJpaEntity entity = loaded.entity();

        entity.applyBalance(domain);
    }
}
