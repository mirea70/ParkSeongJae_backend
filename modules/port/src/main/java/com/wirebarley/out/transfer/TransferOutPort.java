package com.wirebarley.out.transfer;

import com.wirebarley.account.model.AccountId;
import com.wirebarley.in.transfer.result.TransferResult;
import com.wirebarley.transfer.model.Transfer;

import java.time.LocalDate;
import java.util.List;

public interface TransferOutPort {
    void insert(Transfer transfer);
    Long getDailyTransferAmount(AccountId fromAccountId, LocalDate today);
    List<TransferResult> getTransfersBy(AccountId accountId);
}
