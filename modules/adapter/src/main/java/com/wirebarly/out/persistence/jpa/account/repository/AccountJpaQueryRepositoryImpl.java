package com.wirebarly.out.persistence.jpa.account.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wirebarly.account.model.AccountStatus;
import com.wirebarly.out.persistence.jpa.account.entity.AccountJpaEntity;
import com.wirebarly.out.persistence.jpa.account.entity.QAccountJpaEntity;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class AccountJpaQueryRepositoryImpl implements AccountJpaQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QAccountJpaEntity account = QAccountJpaEntity.accountJpaEntity;

    @Override
    public Optional<AccountJpaEntity> findOneActiveForUpdate(Long accountId) {

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(account)
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                        .where(
                                account.accountId.eq(accountId),
                                account.status.eq(AccountStatus.ACTIVE.name()),
                                account.closedAt.isNull()
                        )
                        .fetchOne()
        );
    }
}
