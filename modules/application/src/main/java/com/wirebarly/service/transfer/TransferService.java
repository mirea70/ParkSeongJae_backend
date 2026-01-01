package com.wirebarly.service.transfer;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.AccountTransaction;
import com.wirebarly.account.model.AccountTransactionTransferType;
import com.wirebarly.common.model.Loaded;
import com.wirebarly.in.transfer.command.TransferCreateCommand;
import com.wirebarly.in.transfer.result.TransferResult;
import com.wirebarly.in.transfer.usecase.TransferUseCase;
import com.wirebarly.out.account.AccountOutPort;
import com.wirebarly.out.account.AccountTransactionOutPort;
import com.wirebarly.out.common.IdGenerator;
import com.wirebarly.out.transfer.TransferOutPort;
import com.wirebarly.service.account.AccountService;
import com.wirebarly.transfer.model.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransferService implements TransferUseCase {
    private final AccountService accountService;
    private final IdGenerator idGenerator;
    private final TransferOutPort transferOutPort;
    private final AccountTransactionOutPort accountTransactionOutPort;
    private final AccountOutPort accountOutPort;

    @Override
    public void transfer(TransferCreateCommand command) {
        Long fromId = command.fromAccountId();
        Long toId   = command.toAccountId();

        // 잠금 순서 고정: 작은 ID → 큰 ID
        Long firstId  = Math.min(fromId, toId);
        Long secondId = Math.max(fromId, toId);

        Loaded<Account> first  = accountService.getValidatedAccountForUpdate(firstId);
        Loaded<Account> second = accountService.getValidatedAccountForUpdate(secondId);

        Loaded<Account> loadedFromAccount = fromId.equals(first.domain().getId().getValue()) ? first : second;
        Loaded<Account> loadedToAccount   = loadedFromAccount == first ? second : first;
        Account fromAccount = loadedFromAccount.domain();
        Account toAccount = loadedToAccount.domain();

        LocalDateTime now = LocalDateTime.now();

        Transfer transfer = Transfer.createNew(
                idGenerator.nextId(),
                fromAccount.getId().getValue(),
                toAccount.getId().getValue(),
                command.amount(),
                now,
                transferOutPort.getDailyTransferAmount(fromAccount.getId(), now.toLocalDate())
        );

        List<AccountTransaction> accountTransactionsByTransfers = withdrawFromAccount(fromAccount, transfer, now);
        accountTransactionsByTransfers.add(
                depositToAccount(toAccount, transfer, now)
        );

        transferOutPort.insert(transfer);
        accountTransactionOutPort.insert(accountTransactionsByTransfers);
        accountOutPort.applyBalance(loadedFromAccount);
        accountOutPort.applyBalance(loadedToAccount);
    }

    private List<AccountTransaction> withdrawFromAccount(Account fromAccount, Transfer transfer, LocalDateTime now) {
        Long transferId = transfer.getId().getValue();
        Long transferAmount = transfer.getAmount().getValue();
        Long transferFee = transfer.getFee().getValue();
        Long dailyWithdrawAmount = accountTransactionOutPort.getDailyWithdrawAmount(fromAccount.getId(), now.toLocalDate());

        // 송금액 출금
        AccountTransaction fromAccountWithdrawAmountTransaction = fromAccount.withdraw(
                transferAmount,
                now,
                idGenerator.nextId(),
                dailyWithdrawAmount,
                transferId,
                AccountTransactionTransferType.TRANSFER.name()
        );
        // 송금 수수료 출금
        AccountTransaction fromAccountWithdrawFeeTransaction = fromAccount.withdraw(
                transferFee,
                now,
                idGenerator.nextId(),
                dailyWithdrawAmount + transferAmount,
                transferId,
                AccountTransactionTransferType.TRANSFER_FEE.name()
        );

        return new ArrayList<>(List.of(fromAccountWithdrawAmountTransaction, fromAccountWithdrawFeeTransaction));
    }

    private AccountTransaction depositToAccount(Account toAccount, Transfer transfer, LocalDateTime now) {
        return toAccount.deposit(
                transfer.getAmount().getValue(),
                now,
                idGenerator.nextId(),
                transfer.getId().getValue(),
                AccountTransactionTransferType.TRANSFER.name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferResult> getTransfers(Long accountId) {
        Loaded<Account> loadedAccount = accountService.getValidatedAccount(accountId);
        return transferOutPort.getTransfersBy(loadedAccount.domain().getId());
    }
}
