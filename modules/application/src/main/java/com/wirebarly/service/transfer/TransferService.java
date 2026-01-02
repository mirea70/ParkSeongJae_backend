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
        // 1. 계좌 ID 추출
        Long fromAccountId = command.fromAccountId();
        Long toAccountId = command.toAccountId();

        // 2. 계좌 잠금 및 로드 (데드락 방지)
        boolean isFromAccountSmaller = fromAccountId < toAccountId;
        Long firstLockId = isFromAccountSmaller ? fromAccountId : toAccountId;
        Long secondLockId = isFromAccountSmaller ? toAccountId : fromAccountId;

        Loaded<Account> firstLoadedAccount = accountService.getValidatedAccountForUpdate(firstLockId);
        Loaded<Account> secondLoadedAccount = accountService.getValidatedAccountForUpdate(secondLockId);

        Loaded<Account> loadedFromAccount = isFromAccountSmaller ? firstLoadedAccount : secondLoadedAccount;
        Loaded<Account> loadedToAccount = isFromAccountSmaller ? secondLoadedAccount : firstLoadedAccount;

        Account fromAccount = loadedFromAccount.domain();
        Account toAccount = loadedToAccount.domain();

        // 3. 송금 도메인 객체 생성
        LocalDateTime now = LocalDateTime.now();
        Long dailyTransferAmount = transferOutPort.getDailyTransferAmount(fromAccount.getId(), now.toLocalDate());

        Transfer transfer = Transfer.createNew(
                idGenerator.nextId(),
                fromAccount.getId().getValue(),
                toAccount.getId().getValue(),
                command.amount(),
                now,
                dailyTransferAmount);

        // 4. 출금 및 입금 처리 (도메인 로직 실행)
        List<AccountTransaction> transactionHistory = new ArrayList<>();

        List<AccountTransaction> withdrawTransactions = processWithdrawal(fromAccount, transfer, now);
        AccountTransaction depositTransaction = processDeposit(toAccount, transfer, now);

        transactionHistory.addAll(withdrawTransactions);
        transactionHistory.add(depositTransaction);

        // 5. 변경 사항 저장
        transferOutPort.insert(transfer);
        accountTransactionOutPort.insert(transactionHistory);
        accountOutPort.applyBalance(loadedFromAccount);
        accountOutPort.applyBalance(loadedToAccount);
    }

    private List<AccountTransaction> processWithdrawal(Account fromAccount, Transfer transfer, LocalDateTime now) {
        Long transferId = transfer.getId().getValue();
        Long transferAmount = transfer.getAmount().getValue();
        Long transferFee = transfer.getFee().getValue();
        Long dailyWithdrawAmount = accountTransactionOutPort.getDailyWithdrawAmount(fromAccount.getId(),
                now.toLocalDate());

        // 송금액 출금
        AccountTransaction withdrawalTransaction = fromAccount.withdraw(
                transferAmount,
                now,
                idGenerator.nextId(),
                dailyWithdrawAmount,
                transferId,
                AccountTransactionTransferType.TRANSFER.name());
        // 송금 수수료 출금
        AccountTransaction feeTransaction = fromAccount.withdraw(
                transferFee,
                now,
                idGenerator.nextId(),
                dailyWithdrawAmount + transferAmount,
                transferId,
                AccountTransactionTransferType.TRANSFER_FEE.name());

        return new ArrayList<>(List.of(withdrawalTransaction, feeTransaction));
    }

    private AccountTransaction processDeposit(Account toAccount, Transfer transfer, LocalDateTime now) {
        return toAccount.deposit(
                transfer.getAmount().getValue(),
                now,
                idGenerator.nextId(),
                transfer.getId().getValue(),
                AccountTransactionTransferType.TRANSFER.name());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferResult> getTransfers(Long accountId) {
        Loaded<Account> loadedAccount = accountService.getValidatedAccount(accountId);
        return transferOutPort.getTransfersBy(loadedAccount.domain().getId());
    }
}
