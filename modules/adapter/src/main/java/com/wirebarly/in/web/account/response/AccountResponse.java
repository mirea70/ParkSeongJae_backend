package com.wirebarly.in.web.account.response;

import com.wirebarly.in.account.result.AccountResult;

import java.time.LocalDateTime;
import java.util.Objects;

public record AccountResponse(
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
    public static AccountResponse from(AccountResult result) {
        Objects.requireNonNull(result, "AccountResult must not be null");

        return new AccountResponse(
                result.accountId(),
                result.customerId(),
                result.bankCode(),
                result.accountNumber(),
                result.status(),
                result.balance(),
                result.createdAt(),
                result.updatedAt(),
                result.closedAt()
        );
    }
}
