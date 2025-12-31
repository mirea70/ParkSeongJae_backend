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
    private final AccountTransactionType type;
    private final Money amount;
    private final Money balanceAfter;
    private final LocalDateTime transactedAt;

    static AccountTransaction createNew(Long accountTransactionId, Long accountId, Long transferId, String type, Long amount, Long balanceAfter, LocalDateTime transactedAt) {
        return new AccountTransaction(
                new AccountTransactionId(accountTransactionId),
                new AccountId(accountId),
                transferId != null ? new TransferId(transferId) : null,
                AccountTransactionType.valueOf(type),
                new Money(amount),
                new Money(balanceAfter),
                transactedAt
        );
    }
}
