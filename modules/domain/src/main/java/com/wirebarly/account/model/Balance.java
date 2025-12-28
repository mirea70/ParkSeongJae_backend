package com.wirebarly.account.model;

import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.AccountErrorInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Balance {
    private long value;

    public Balance(Long input) {
        validate(input);
        this.value = input;
    }

    private void validate(Long input) {
        if(input == null)
            throw new DomainException(AccountErrorInfo.BALANCE_NOT_EXIST);
        if(input < 0)
            throw new DomainException(AccountErrorInfo.BALANCE_NOT_POSITIVE);
    }

    static Balance init() {
        return new Balance(0);
    }
}
