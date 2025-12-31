package com.wirebarly.in.account.usecase;

import com.wirebarly.in.account.command.AccountCreateCommand;
import com.wirebarly.in.account.command.AccountDepositCommand;
import com.wirebarly.in.account.result.AccountResult;

public interface AccountUseCase {
    AccountResult register(AccountCreateCommand command);
    void remove(Long accountId);
    void deposit(Long accountId, AccountDepositCommand command);
}
