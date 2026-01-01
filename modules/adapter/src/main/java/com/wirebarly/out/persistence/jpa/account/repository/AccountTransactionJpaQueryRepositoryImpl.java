package com.wirebarly.out.persistence.jpa.account.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wirebarly.out.persistence.jpa.account.entity.QAccountTransactionJpaEntity;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class AccountTransactionJpaQueryRepositoryImpl implements AccountTransactionJpaQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QAccountTransactionJpaEntity accountTransaction = QAccountTransactionJpaEntity.accountTransactionJpaEntity;

    public Long getSumByTypeBetween(Long accountId, String type, LocalDateTime from, LocalDateTime to) {
        return queryFactory
                .select(accountTransaction.amount.sum().coalesce(0L))
                .from(accountTransaction)
                .where(
                        accountTransaction.accountId.eq(accountId),
                        accountTransaction.type.eq(type),
                        accountTransaction.transactedAt.goe(from),
                        accountTransaction.transactedAt.lt(to)
                )
                .fetchOne();
    }
}
