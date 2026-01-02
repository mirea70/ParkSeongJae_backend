package com.wirebarley.in.transfer.usecase;

import com.wirebarley.in.transfer.command.TransferCreateCommand;
import com.wirebarley.in.transfer.result.TransferResult;

import java.util.List;

public interface TransferUseCase {
    void transfer(TransferCreateCommand command);
    List<TransferResult> getTransfers(Long accountId);
}
