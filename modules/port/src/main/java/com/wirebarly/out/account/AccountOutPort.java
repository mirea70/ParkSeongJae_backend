package com.wirebarly.out.account;

import com.wirebarly.account.model.Account;

public interface AccountOutPort {
    Account save(Account account);
}
