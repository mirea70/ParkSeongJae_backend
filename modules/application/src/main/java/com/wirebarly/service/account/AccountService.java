package com.wirebarly.service.account;

import com.wirebarly.account.model.Account;
import com.wirebarly.customer.model.CustomerId;
import com.wirebarly.error.info.CustomerErrorInfo;
import com.wirebarly.in.account.command.AccountCreateCommand;
import com.wirebarly.in.account.result.AccountResult;
import com.wirebarly.in.account.usecase.AccountUseCase;
import com.wirebarly.out.account.AccountOutPort;
import com.wirebarly.out.common.IdGenerator;
import com.wirebarly.out.customer.CustomerOutPort;
import com.wirebarly.error.exception.BusinessException;
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
    private final IdGenerator idGenerator;

    @Override
    public AccountResult register(AccountCreateCommand command) {
        if(!customerOutPort.isExist(new CustomerId(command.customerId()))) {
            throw new BusinessException(CustomerErrorInfo.NOT_FOUND);
        }

        Account savedAccount = accountOutPort.save(
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
}
