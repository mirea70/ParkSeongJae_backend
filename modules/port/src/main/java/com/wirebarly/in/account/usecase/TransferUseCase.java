package com.wirebarly.in.account.usecase;

import com.wirebarly.in.account.command.TransferCreateCommand;

public interface TransferUseCase {
    void transfer(TransferCreateCommand command);
}
