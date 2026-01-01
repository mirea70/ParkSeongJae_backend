package com.wirebarly.out.transfer;

import com.wirebarly.account.model.AccountId;
import com.wirebarly.in.transfer.result.TransferResult;
import com.wirebarly.transfer.model.Transfer;

import java.time.LocalDate;
import java.util.List;

public interface TransferOutPort {
    void insert(Transfer transfer);
    Long getDailyTransferAmount(AccountId fromAccountId, LocalDate today);
    List<TransferResult> getTransfersBy(AccountId accountId);
}
