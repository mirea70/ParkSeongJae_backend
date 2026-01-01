package com.wirebarly.out.account;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.AccountId;
import com.wirebarly.common.model.Loaded;

import java.util.Optional;

public interface AccountOutPort {
    Account insert(Account account);
//    Optional<Account> loadOne(AccountId id);
    Optional<Loaded<Account>> loadOne(AccountId id);
    Optional<Loaded<Account>> loadOneForUpdate(AccountId id);
    void applyClose(Loaded<Account> loadedAccount);
    void applyBalance(Loaded<Account> loadedAccount);
}
