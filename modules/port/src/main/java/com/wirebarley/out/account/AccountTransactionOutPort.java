package com.wirebarley.out.account;

import com.wirebarley.account.model.AccountId;
import com.wirebarley.account.model.AccountTransaction;

import java.time.LocalDate;
import java.util.List;

public interface AccountTransactionOutPort {
    void insert(AccountTransaction accountTransaction);
    void insert(List<AccountTransaction> accountTransactions);
    Long getDailyWithdrawAmount(AccountId accountId, LocalDate today);
}
