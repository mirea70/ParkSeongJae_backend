package com.wirebarly.in.account.command;

public record TransferCreateCommand(
        Long fromAccountId,
        Long toAccountId,
        Long amount
) {}
