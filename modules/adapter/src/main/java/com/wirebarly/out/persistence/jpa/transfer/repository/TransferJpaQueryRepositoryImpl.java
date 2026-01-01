package com.wirebarly.out.persistence.jpa.transfer.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wirebarly.out.persistence.jpa.transfer.entity.QTransferJpaEntity;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class TransferJpaQueryRepositoryImpl implements TransferJpaQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QTransferJpaEntity transfer = QTransferJpaEntity.transferJpaEntity;

    @Override
    public Long getSumByFromAccountBetween(Long fromAccountId, LocalDateTime from, LocalDateTime to) {
        return queryFactory
                .select(transfer.amount.sum().coalesce(0L))
                .from(transfer)
                .where(
                        transfer.fromAccountId.eq(fromAccountId),
                        transfer.transferredAt.goe(from),
                        transfer.transferredAt.lt(to)
                )
                .fetchOne();
    }
}
