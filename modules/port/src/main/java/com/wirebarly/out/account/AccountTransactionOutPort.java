package com.wirebarly.out.account;

import com.wirebarly.account.model.AccountTransaction;

public interface AccountTransactionOutPort {
    void insert(AccountTransaction accountTransaction);
}
