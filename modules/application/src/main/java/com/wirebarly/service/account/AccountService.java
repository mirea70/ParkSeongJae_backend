package com.wirebarly.service.account;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.AccountId;
import com.wirebarly.account.model.AccountTransaction;
import com.wirebarly.common.model.Loaded;
import com.wirebarly.customer.model.CustomerId;
import com.wirebarly.error.exception.BusinessException;
import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.AccountErrorInfo;
import com.wirebarly.error.info.CustomerErrorInfo;
import com.wirebarly.in.account.command.AccountCreateCommand;
import com.wirebarly.in.account.command.AccountDepositCommand;
import com.wirebarly.in.account.command.AccountWithdrawCommand;
import com.wirebarly.in.account.result.AccountResult;
import com.wirebarly.in.account.usecase.AccountUseCase;
import com.wirebarly.out.account.AccountOutPort;
import com.wirebarly.out.account.AccountTransactionOutPort;
import com.wirebarly.out.common.IdGenerator;
import com.wirebarly.out.customer.CustomerOutPort;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

        Account savedAccount;

        try {
            savedAccount = accountOutPort.insert(
                    Account.createNew(
                            idGenerator.nextId(),
                            command.customerId(),
                            command.bankCode(),
                            command.accountNumber(),
                            LocalDateTime.now()
                    )
            );
        } catch (DataIntegrityViolationException e) {
            throw new DomainException(AccountErrorInfo.DUPLICATED);
        }

        return AccountResult.from(savedAccount);
    }

    @Override
    public void remove(Long accountId) {
        Loaded<Account> loadedAccount = getValidatedAccountForUpdate(accountId);

        loadedAccount.domain().close(LocalDateTime.now());
        accountOutPort.applyClose(loadedAccount);
    }

    @Override
    public void deposit(Long accountId, AccountDepositCommand command) {
        Loaded<Account> loadedAccount = getValidatedAccountForUpdate(accountId);

        Long accountTransactionId = idGenerator.nextId();
        AccountTransaction accountTransaction = loadedAccount.domain().deposit(command.amount(), LocalDateTime.now(), accountTransactionId);

        accountOutPort.applyBalance(loadedAccount);
        accountTransactionOutPort.insert(accountTransaction);
    }

    @Override
    public void withdraw(Long accountId, AccountWithdrawCommand command) {
        Loaded<Account> loadedAccount = getValidatedAccountForUpdate(accountId);
        Account account = loadedAccount.domain();

        Long accountTransactionId = idGenerator.nextId();

        LocalDate today = LocalDateTime.now().toLocalDate();
        Long dailyWithdrawAmount = accountTransactionOutPort.getDailyWithdrawAmount(account.getId(), today);
        AccountTransaction accountTransaction = account.withdraw(command.amount(), LocalDateTime.now(), accountTransactionId, dailyWithdrawAmount);

        accountOutPort.applyBalance(loadedAccount);
        accountTransactionOutPort.insert(accountTransaction);
    }

    public Loaded<Account> getValidatedAccount(Long accountId) {
        Loaded<Account> loadedAccount = accountOutPort.loadOne(new AccountId(accountId))
                .orElseThrow(() -> new BusinessException(AccountErrorInfo.NOT_FOUND));

        if(!customerOutPort.isExist(loadedAccount.domain().getCustomerId())) {
            throw new BusinessException(CustomerErrorInfo.NOT_FOUND);
        }
        return loadedAccount;
    }

    public Loaded<Account> getValidatedAccountForUpdate(Long accountId) {
        Loaded<Account> loadedAccount = accountOutPort.loadOneForUpdate(new AccountId(accountId))
                .orElseThrow(() -> new BusinessException(AccountErrorInfo.NOT_FOUND));

        if(!customerOutPort.isExist(loadedAccount.domain().getCustomerId())) {
            throw new BusinessException(CustomerErrorInfo.NOT_FOUND);
        }
        return loadedAccount;
    }
}
