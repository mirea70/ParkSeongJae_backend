package com.wirebarly.service.account;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.AccountId;
import com.wirebarly.account.model.AccountTransaction;
import com.wirebarly.customer.model.CustomerId;
import com.wirebarly.error.exception.BusinessException;
import com.wirebarly.error.info.AccountErrorInfo;
import com.wirebarly.error.info.CustomerErrorInfo;
import com.wirebarly.in.account.command.AccountCreateCommand;
import com.wirebarly.in.account.command.AccountDepositCommand;
import com.wirebarly.in.account.result.AccountResult;
import com.wirebarly.in.account.usecase.AccountUseCase;
import com.wirebarly.out.account.AccountOutPort;
import com.wirebarly.out.account.AccountTransactionOutPort;
import com.wirebarly.out.common.IdGenerator;
import com.wirebarly.out.customer.CustomerOutPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService implements AccountUseCase {
    private final CustomerOutPort customerOutPort;
    private final AccountOutPort accountOutPort;
    private final AccountTransactionOutPort accountTransactionOutPort;
    private final IdGenerator idGenerator;

    @Override
    public AccountResult register(AccountCreateCommand command) {
        if(!customerOutPort.isExist(new CustomerId(command.customerId()))) {
            throw new BusinessException(CustomerErrorInfo.NOT_FOUND);
        }

        Account savedAccount = accountOutPort.insert(
                Account.createNew(
                        idGenerator.nextId(),
                        command.customerId(),
                        command.bankCode(),
                        command.accountNumber(),
                        LocalDateTime.now()
                )
        );

        return AccountResult.from(savedAccount);
    }

    @Override
    public void remove(Long accountId) {
        Account account = getValidatedAccount(accountId);

        account.close(LocalDateTime.now());
        accountOutPort.update(account);
    }

    @Override
    public void deposit(Long accountId, AccountDepositCommand command) {
        Account account = getValidatedAccount(accountId);

        Long accountTransactionId = idGenerator.nextId();
        AccountTransaction accountTransaction = account.deposit(command.amount(), LocalDateTime.now(), accountTransactionId);

        accountTransactionOutPort.insert(accountTransaction);
        accountOutPort.update(account);
    }

    private Account getValidatedAccount(Long accountId) {
        Account account = accountOutPort.loadOne(new AccountId(accountId))
                .orElseThrow(() -> new BusinessException(AccountErrorInfo.NOT_FOUND));

        if(!customerOutPort.isExist(account.getCustomerId())) {
            throw new BusinessException(CustomerErrorInfo.NOT_FOUND);
        }
        return account;
    }
}
