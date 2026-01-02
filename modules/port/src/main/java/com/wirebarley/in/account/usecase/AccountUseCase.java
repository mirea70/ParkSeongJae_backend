package com.wirebarley.in.account.usecase;

import com.wirebarley.in.account.command.AccountCreateCommand;
import com.wirebarley.in.account.command.AccountDepositCommand;
import com.wirebarley.in.account.command.AccountWithdrawCommand;
import com.wirebarley.in.account.result.AccountResult;

public interface AccountUseCase {
    AccountResult register(AccountCreateCommand command);
    void remove(Long accountId);
    void deposit(Long accountId, AccountDepositCommand command);
    void withdraw(Long accountId, AccountWithdrawCommand command);
}
