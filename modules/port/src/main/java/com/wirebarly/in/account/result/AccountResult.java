package com.wirebarly.in.account.result;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.BankInfo;

import java.time.LocalDateTime;
import java.util.Objects;

public record AccountResult(
    Long accountId,
    Long customerId,
    String bankCode,
    String accountNumber,
    String status,
    Long balance,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime closedAt
) {
    public static AccountResult from(Account account) {
        Objects.requireNonNull(account, "Account must not be null");

        BankInfo bankInfo = account.getBankInfo();
        return new AccountResult(
                account.getId().getValue(),
                account.getCustomerId().getValue(),
                bankInfo.getBankCode().getCode(),
                bankInfo.getAccountNumber().getValue(),
                account.getStatus().name(),
                account.getBalance().getValue(),
                account.getCreatedAt(),
                account.getUpdatedAt(),
                account.getClosedAt()
        );
    }
}
