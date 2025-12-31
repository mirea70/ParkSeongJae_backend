package com.wirebarly.common.model;

import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.CommonErrorInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Money {
    private final long value;

    public Money(Long input) {
        validate(input);
        this.value = input;
    }

    public Money plus(Long input) {
        validate(input);
        return new Money(this.value + input);
    }

    public Money minus(Long input) {
        validate(input);
        return new Money(this.value - input);
    }

    static void validate(Long input) {
        if(input == null)
            throw new DomainException(CommonErrorInfo.Money_NOT_EXIST);
        if(input < 0)
            throw new DomainException(CommonErrorInfo.Money_NOT_POSITIVE);
    }
}
