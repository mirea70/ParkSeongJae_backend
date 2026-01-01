package com.wirebarly.in.web.account.request;

import com.wirebarly.in.account.command.TransferCreateCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferCreateRequest(
        @Positive(message = "계좌 시스템 ID의 값은 양의 정수여야합니다.")
        @NotNull(message = "계좌 시스템 ID의 값이 존재하지 않습니다.")
        Long toAccountId,

        @Positive(message = "송금액은 양의 정수여야합니다.")
        @NotNull(message = "송금액의 값이 존재하지 않습니다.")
        Long amount
) {
    public TransferCreateCommand toCommand(Long fromAccountId) {
        return new TransferCreateCommand(
                fromAccountId,
                toAccountId,
                amount
        );
    }
}
