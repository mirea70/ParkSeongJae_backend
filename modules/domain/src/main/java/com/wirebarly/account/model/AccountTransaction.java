package com.wirebarly.account.model;

import com.wirebarly.common.model.Money;
import com.wirebarly.transfer.model.TransferId;
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
    private final AccountId counterpartyAccountId;
    private final AccountTransactionType type;
    private final AccountTransactionDirection direction;
    private final Money amount;
    private final Money balanceAfter;
    private final LocalDateTime transactedAt;

    static AccountTransaction createNew(Long accountTransactionId, Long accountId, Long transferId, Long counterpartyAccountId,
                                               String type, String direction, Long amount, Long balanceAfter, LocalDateTime transactedAt) {
        return new AccountTransaction(
                new AccountTransactionId(accountTransactionId),
                new AccountId(accountId),
                transferId != null ? new TransferId(transferId) : null,
                counterpartyAccountId != null ? new AccountId(counterpartyAccountId) : null,
                AccountTransactionType.valueOf(type),
                AccountTransactionDirection.valueOf(direction),
                new Money(amount),
                new Money(balanceAfter),
                transactedAt
        );
    }
}
