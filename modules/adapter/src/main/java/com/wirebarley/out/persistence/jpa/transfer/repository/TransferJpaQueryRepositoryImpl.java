package com.wirebarley.out.persistence.jpa.transfer.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wirebarley.in.transfer.result.TransferResult;
import com.wirebarley.out.persistence.jpa.transfer.entity.QTransferJpaEntity;
import com.wirebarley.transfer.model.TransferDirection;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public List<TransferResult> findAllByAccountId(Long accountId) {
        BooleanExpression isOut = transfer.fromAccountId.eq(accountId);

        return queryFactory
                .select(Projections.constructor(TransferResult.class,
                        transfer.transferId,
                        new CaseBuilder().when(isOut).then(TransferDirection.OUT.name()).otherwise(TransferDirection.IN.name()),
                        new CaseBuilder().when(isOut).then(transfer.toAccountId).otherwise(transfer.fromAccountId),
                        transfer.amount,
                        new CaseBuilder().when(isOut).then(transfer.fee).otherwise(0L),
                        transfer.transferredAt
                        ))
                .from(transfer)
                .where(transfer.fromAccountId.eq(accountId)
                        .or(transfer.toAccountId.eq(accountId))
                ).orderBy(transfer.transferredAt.desc())
                .fetch();
    }
}
