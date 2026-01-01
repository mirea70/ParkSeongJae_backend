package com.wirebarly.service.transfer;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.AccountTransaction;
import com.wirebarly.account.model.AccountTransactionTransferType;
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
        Account fromAccount = accountService.getValidatedAccount(command.fromAccountId());
        Account toAccount = accountService.getValidatedAccount(command.toAccountId());
        LocalDateTime now = LocalDateTime.now();

        Transfer transfer = Transfer.createNew(
                idGenerator.nextId(),
                fromAccount.getId().getValue(),
                toAccount.getId().getValue(),
                command.amount(),
                now,
                transferOutPort.getDailyTransferAmount(fromAccount.getId(), now.toLocalDate())
        );

        List<AccountTransaction> accountTransactionsByTransfer = withdrawFromAccount(fromAccount, transfer, now);
        accountTransactionsByTransfer.add(
                depositToAccount(toAccount, transfer, now)
        );

        transferOutPort.insert(transfer);
        accountTransactionOutPort.insert(accountTransactionsByTransfer);
        accountOutPort.update(fromAccount);
        accountOutPort.update(toAccount);
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
        Account account = accountService.getValidatedAccount(accountId);
        return transferOutPort.getTransfersBy(account.getId());
    }
}
