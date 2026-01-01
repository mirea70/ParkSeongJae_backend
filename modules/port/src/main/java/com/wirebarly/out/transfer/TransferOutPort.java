package com.wirebarly.out.transfer;

import com.wirebarly.account.model.AccountId;
import com.wirebarly.transfer.model.Transfer;

import java.time.LocalDate;

public interface TransferOutPort {
    void insert(Transfer transfer);
    Long getDailyTransferAmount(AccountId fromAccountId, LocalDate today);
}
