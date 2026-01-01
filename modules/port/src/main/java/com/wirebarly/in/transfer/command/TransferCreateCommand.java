package com.wirebarly.in.transfer.command;

public record TransferCreateCommand(
        Long fromAccountId,
        Long toAccountId,
        Long amount
) {}
