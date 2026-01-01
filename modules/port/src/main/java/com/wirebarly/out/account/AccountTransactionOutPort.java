package com.wirebarly.out.account;

import com.wirebarly.account.model.AccountId;
import com.wirebarly.account.model.AccountTransaction;

import java.time.LocalDate;
import java.util.List;

public interface AccountTransactionOutPort {
    void insert(AccountTransaction accountTransaction);
    void insert(List<AccountTransaction> accountTransactions);
    Long getDailyWithdrawAmount(AccountId accountId, LocalDate today);
}
