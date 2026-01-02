package com.wirebarly.transfer.model;

import com.wirebarly.account.model.AccountId;
import com.wirebarly.common.model.Money;
import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.TransferErrorInfo;
import com.wirebarly.transfer.policy.TransferPolicy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Transfer {
    private final TransferId id;
    private final AccountId fromAccountId;
    private final AccountId toAccountId;
    private final Money amount;
    private final Money fee;
    private final LocalDateTime transferredAt;

    public static Transfer createNew(Long id, Long fromAccountId, Long toAccountId, Long amount, LocalDateTime now, Long dailyTransferAmount) {
        Money transferAmount = new Money(amount);
        if(transferAmount.isLessThan(new Money(TransferPolicy.TRANSFER_MIN_AMOUNT))) {
            throw new DomainException(TransferErrorInfo.TOO_SMALL_TRANSFER_AMOUNT);
        }

        Money dailyUsed = new Money(dailyTransferAmount);
        Money limit = new Money(TransferPolicy.DAILY_TRANSFER_LIMIT);

        Money afterTransfer = dailyUsed.plus(transferAmount);

        if (afterTransfer.isGreaterThan(limit)) {
            Money overAmount = afterTransfer.minus(limit);
            throw new DomainException(
                    TransferErrorInfo.OVER_TRANSFER_LIMIT,
                    Map.of(
                            "limit", limit.getValue(),
                            "overAmount", overAmount.getValue()
                    )
            );
        }

        Money fee = TransferPolicy.calculateFee(transferAmount);

        return new Transfer(
                new TransferId(id),
                new AccountId(fromAccountId),
                new AccountId(toAccountId),
                transferAmount,
                fee,
                now
        );
    }
}
