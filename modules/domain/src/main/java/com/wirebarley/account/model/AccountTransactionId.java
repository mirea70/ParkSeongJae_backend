package com.wirebarley.account.model;

import com.wirebarley.error.exception.DomainException;
import com.wirebarley.error.info.AccountTransactionErrorInfo;
import lombok.Getter;

@Getter
public class AccountTransactionId {
    private final long value;

    AccountTransactionId(Long input) {
        validate(input);
        this.value = input;
    }

    private void validate(Long input) {
        if (input == null)
            throw new DomainException(AccountTransactionErrorInfo.ID_NOT_EXIST);
        if (input <= 0) {
            throw new DomainException(AccountTransactionErrorInfo.ID_NOT_POSITIVE);
        }
    }
}
