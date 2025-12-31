package com.wirebarly.account.model;

import com.wirebarly.common.model.Money;
import com.wirebarly.customer.model.CustomerId;
import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.AccountErrorInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {
    private final AccountId id;
    private final CustomerId customerId;
    private final BankInfo bankInfo;

    private AccountStatus status;
    private Money balance;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;

    public static Account createNew(Long idInput, Long customerIdInput, String bankCode, String accountNumber, LocalDateTime now) {
        return new Account(
                new AccountId(idInput),
                new CustomerId(customerIdInput),
                BankInfo.of(bankCode, accountNumber),
                AccountStatus.ACTIVE,
                new Money(0L),
                now,
                now,
                null
        );
    }

    public static Account fromOutside(Long accountId, Long customerId, String bankCode, String accountNumber, String status, Long balance, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime closedAt) {
        return new Account(
                new AccountId(accountId),
                new CustomerId(customerId),
                BankInfo.of(bankCode, accountNumber),
                AccountStatus.valueOf(status),
                new Money(balance),
                createdAt,
                updatedAt,
                closedAt
        );
    }

    public void close(LocalDateTime now) {
        this.status = AccountStatus.CLOSED;
        this.closedAt = now;
    }

    private boolean isClosed() {
        return this.status == AccountStatus.CLOSED || this.closedAt != null;
    }

    public AccountTransaction deposit(Long amount, LocalDateTime now, Long accountTransactionId) {
        if(amount == null) {
            throw new DomainException(AccountErrorInfo.DEPOSIT_NOT_EXIST);
        }
        if(amount <= 0) {
            throw new DomainException(AccountErrorInfo.DEPOSIT_NOT_POSITIVE);
        }
        if(isClosed()) {
            throw new DomainException(AccountErrorInfo.CLOSED);
        }

        this.balance = new Money(this.balance.getValue() + amount);
        this.updatedAt = now;

        return AccountTransaction.createNew(
                accountTransactionId,
                this.id.getValue(),
                null,
                null,
                AccountTransactionType.DEPOSIT.name(),
                AccountTransactionDirection.IN.name(),
                amount,
                this.balance.getValue(),
                now
        );
    }
}
