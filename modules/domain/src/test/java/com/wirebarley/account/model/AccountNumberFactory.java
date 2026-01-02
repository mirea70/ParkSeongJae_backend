package com.wirebarley.account.model;

public final class AccountNumberFactory {
    private AccountNumberFactory() {}

    public static AccountNumber of(String value) {
        return new AccountNumber(value);
    }
}
