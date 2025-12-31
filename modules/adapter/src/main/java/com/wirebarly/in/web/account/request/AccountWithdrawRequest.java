package com.wirebarly.in.web.account.request;

import com.wirebarly.in.account.command.AccountWithdrawCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AccountWithdrawRequest(
        @Positive(message = "입금 금액은 양의 정수여야합니다.")
        @NotNull(message = "입금 금액의 값이 존재하지 않습니다.")
        Long amount
) {
    public AccountWithdrawCommand toCommand() {
        return new AccountWithdrawCommand(amount);
    }
}
