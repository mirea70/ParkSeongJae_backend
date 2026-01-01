package com.wirebarly.in.transfer.usecase;

import com.wirebarly.in.transfer.command.TransferCreateCommand;
import com.wirebarly.in.transfer.result.TransferResult;

import java.util.List;

public interface TransferUseCase {
    void transfer(TransferCreateCommand command);
    List<TransferResult> getTransfers(Long accountId);
}
