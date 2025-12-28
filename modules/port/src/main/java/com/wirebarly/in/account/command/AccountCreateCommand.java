package com.wirebarly.in.account.command;

public record AccountCreateCommand(
        Long customerId,
        String bankCode,
        String accountNumber
) {}
