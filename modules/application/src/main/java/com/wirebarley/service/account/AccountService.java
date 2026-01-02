package com.wirebarley.service.account;

import com.wirebarley.account.model.Account;
import com.wirebarley.account.model.AccountId;
import com.wirebarley.account.model.AccountTransaction;
import com.wirebarley.common.model.Loaded;
import com.wirebarley.customer.model.CustomerId;
import com.wirebarley.error.exception.BusinessException;
import com.wirebarley.error.exception.DomainException;
import com.wirebarley.error.info.AccountErrorInfo;
import com.wirebarley.error.info.CustomerErrorInfo;
import com.wirebarley.in.account.command.AccountCreateCommand;
import com.wirebarley.in.account.command.AccountDepositCommand;
import com.wirebarley.in.account.command.AccountWithdrawCommand;
import com.wirebarley.in.account.result.AccountResult;
import com.wirebarley.in.account.usecase.AccountUseCase;
import com.wirebarley.out.account.AccountOutPort;
import com.wirebarley.out.account.AccountTransactionOutPort;
import com.wirebarley.out.common.IdGenerator;
import com.wirebarley.out.customer.CustomerOutPort;
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
