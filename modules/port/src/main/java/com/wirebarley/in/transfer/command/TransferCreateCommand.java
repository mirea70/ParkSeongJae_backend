package com.wirebarley.in.transfer.command;

public record TransferCreateCommand(
        Long fromAccountId,
        Long toAccountId,
        Long amount
) {}
