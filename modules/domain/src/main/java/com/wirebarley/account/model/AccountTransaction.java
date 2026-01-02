package com.wirebarley.account.model;

import com.wirebarley.common.model.Money;
import com.wirebarley.transfer.model.TransferId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountTransaction {
    private final AccountTransactionId id;
    private final AccountId accountId;
    private final TransferId transferId;
    private final AccountTransactionType type;
    private final AccountTransactionTransferType transferType;
    private final Money amount;
    private final Money balanceAfter;
    private final LocalDateTime transactedAt;

    static AccountTransaction createNew(Long id, Long accountId, Long transferId, String type, String transferType, Long amount, Long balanceAfter, LocalDateTime transactedAt) {
        return new AccountTransaction(
                new AccountTransactionId(id),
                new AccountId(accountId),
                transferId != null ? new TransferId(transferId) : null,
                AccountTransactionType.from(type),
                AccountTransactionTransferType.from(transferType),
                new Money(amount),
                new Money(balanceAfter),
                transactedAt
        );
    }
}
