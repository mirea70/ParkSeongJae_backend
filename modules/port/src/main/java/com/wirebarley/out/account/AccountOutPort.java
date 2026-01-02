package com.wirebarley.out.account;

import com.wirebarley.account.model.Account;
import com.wirebarley.account.model.AccountId;
import com.wirebarley.common.model.Loaded;

import java.util.Optional;

public interface AccountOutPort {
    Account insert(Account account);
    Optional<Loaded<Account>> loadOne(AccountId id);
    Optional<Loaded<Account>> loadOneForUpdate(AccountId id);
    void applyClose(Loaded<Account> loadedAccount);
    void applyBalance(Loaded<Account> loadedAccount);
}
