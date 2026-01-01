package com.wirebarly.account.model;

import com.wirebarly.account.policy.AccountPolicy;
import com.wirebarly.common.model.Money;
import com.wirebarly.customer.model.CustomerId;
import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.AccountErrorInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

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

    public static Account createNew(Long id, Long customerId, String bankCode, String accountNumber, LocalDateTime now) {
        return new Account(
                new AccountId(id),
                new CustomerId(customerId),
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
        return this.deposit(amount, now, accountTransactionId, null, null);
    }

    public AccountTransaction deposit(Long amount, LocalDateTime now, Long accountTransactionId, Long transferId, String transferType) {

        if(amount == null) {
            throw new DomainException(AccountErrorInfo.DEPOSIT_NOT_EXIST);
        }
        if(amount <= 0) {
            throw new DomainException(AccountErrorInfo.DEPOSIT_NOT_POSITIVE);
        }
        if(isClosed()) {
            throw new DomainException(AccountErrorInfo.CLOSED);
        }

        Money depositAmount = new Money(amount);

        this.balance = this.balance.plus(depositAmount);
        this.updatedAt = now;

        return AccountTransaction.createNew(
                accountTransactionId,
                this.id.getValue(),
                transferId,
                AccountTransactionType.DEPOSIT.name(),
                transferType != null ? transferType : AccountTransactionTransferType.NONE.name(),
                amount,
                this.balance.getValue(),
                now
        );
    }

    public AccountTransaction withdraw(Long amount, LocalDateTime now, Long accountTransactionId, Long dalyWithDrawAmount) {
        return this.withdraw(amount, now, accountTransactionId, dalyWithDrawAmount, null, null);
    }

    public AccountTransaction withdraw(Long amount, LocalDateTime now, Long accountTransactionId, Long dalyWithDrawAmount, Long transferId, String transferType) {
        if(amount == null) {
            throw new DomainException(AccountErrorInfo.WITHDRAW_NOT_EXIST);
        }
        if(amount <= 0) {
            throw new DomainException(AccountErrorInfo.WITHDRAW_NOT_POSITIVE);
        }
        if(isClosed()) {
            throw new DomainException(AccountErrorInfo.CLOSED);
        }

        Money withdrawAmount = new Money(amount);
        Money dailyUsed = new Money(dalyWithDrawAmount);
        Money limit = new Money(AccountPolicy.ACCOUNT_WITHDRAW_DAILY_LIMIT);

        if(withdrawAmount.isGreaterThan(this.balance)) {
            throw new DomainException(AccountErrorInfo.LACK_BALANCE);
        }
        Money dailyUsing = dailyUsed.plus(withdrawAmount);

        if(dailyUsing.isGreaterThan(limit)) {
            Money overAmount = dailyUsing.minus(limit);
            throw new DomainException(
                    AccountErrorInfo.OVER_WITHDRAW_LIMIT,
                    Map.of(
                            "limit", AccountPolicy.ACCOUNT_WITHDRAW_DAILY_LIMIT,
                            "overAmount", overAmount)
            );
        }

        this.balance = this.balance.minus(withdrawAmount);
        this.updatedAt = now;

        return AccountTransaction.createNew(
                accountTransactionId,
                this.id.getValue(),
                transferId,
                AccountTransactionType.WITHDRAW.name(),
                transferType != null ? transferType : AccountTransactionTransferType.NONE.name(),
                amount,
                this.balance.getValue(),
                now
        );
    }
}
