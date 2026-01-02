package com.wirebarley.in.web.account.request;

import com.wirebarley.in.account.command.AccountDepositCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AccountDepositRequest(
        @Positive(message = "입금 금액은 양의 정수여야합니다.")
        @NotNull(message = "입금 금액의 값이 존재하지 않습니다.")
        Long amount
) {
    public AccountDepositCommand toCommand() {
        return new AccountDepositCommand(amount);
    }
}
