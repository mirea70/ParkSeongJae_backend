package com.wirebarly.out.account;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.AccountId;

import java.util.Optional;

public interface AccountOutPort {
    Account insert(Account account);
    Optional<Account> loadOne(AccountId id);
    void update(Account account);
}
