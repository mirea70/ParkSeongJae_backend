package com.wirebarly.in.web.account.request;

import com.wirebarly.in.account.command.AccountCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AccountCreateRequest(
        @Positive(message = "고객 System ID 값은 양의 정수여야합니다.")
        @NotNull(message = "고객 System ID 값은 존재해야합니다.")
        Long customerId,

        @NotBlank(message = "은행코드는 값이 존재해야합니다.")
        String bankCode,

        @NotBlank(message = "계좌번호는 값이 존재해야합니다.")
        @Size(min = 10, max = 20, message = "계좌번호는 10~20자리여야 합니다.")
        String accountNumber
) {
    public AccountCreateCommand toCommand() {
        return new AccountCreateCommand(
                customerId,
                bankCode,
                accountNumber
        );
    }
}
