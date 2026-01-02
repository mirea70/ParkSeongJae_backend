package com.wirebarley.account.model;

import com.wirebarley.error.exception.DomainException;
import com.wirebarley.error.info.AccountErrorInfo;
import lombok.Getter;

@Getter
public class AccountId {
    private final long value;

    public AccountId(Long input) {
        validate(input);
        this.value = input;
    }

    private void validate(Long input) {
        if (input == null)
            throw new DomainException(AccountErrorInfo.ID_NOT_EXIST);
        if (input <= 0) {
            throw new DomainException(AccountErrorInfo.ID_NOT_POSITIVE);
        }
    }

}
