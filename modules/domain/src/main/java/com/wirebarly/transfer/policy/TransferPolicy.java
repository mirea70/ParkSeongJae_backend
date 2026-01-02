package com.wirebarly.transfer.policy;

import com.wirebarly.common.model.Money;

public class TransferPolicy {
    public static final long DAILY_TRANSFER_LIMIT = 3000000;
    public static final long TRANSFER_MIN_AMOUNT = 100;
    public static final int FEE_PERCENT = 1;

    public static Money calculateFee(Money amount) {
        return new Money(amount.getValue() * FEE_PERCENT / 100);
    }
}
