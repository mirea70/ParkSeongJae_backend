package com.wirebarly.out.account;

import com.wirebarly.account.model.AccountId;
import com.wirebarly.account.model.AccountTransaction;

import java.time.LocalDate;

public interface AccountTransactionOutPort {
    void insert(AccountTransaction accountTransaction);
    Long getDailyWithdrawAmount(AccountId accountId, LocalDate today);
}
