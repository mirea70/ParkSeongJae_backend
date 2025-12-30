package com.wirebarly.account.model;

import com.wirebarly.customer.model.CustomerId;
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
    private Balance balance;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;

    public static Account createNew(Long idInput, Long customerIdInput, String bankCode, String accountNumber, LocalDateTime now) {
        return new Account(
                new AccountId(idInput),
                new CustomerId(customerIdInput),
                BankInfo.of(bankCode, accountNumber),
                AccountStatus.ACTIVE,
                Balance.init(),
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
                new Balance(balance),
                createdAt,
                updatedAt,
                closedAt
        );
    }

    public void close(LocalDateTime now) {
        this.status = AccountStatus.CLOSED;
        this.closedAt = now;
    }
}
