package com.wirebarly.common.model;

import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.AccountErrorInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Money {
    private long value;

    public Money(Long input) {
        validate(input);
        this.value = input;
    }

    private void validate(Long input) {
        if(input == null)
            throw new DomainException(AccountErrorInfo.BALANCE_NOT_EXIST);
        if(input < 0)
            throw new DomainException(AccountErrorInfo.BALANCE_NOT_POSITIVE);
    }
}
