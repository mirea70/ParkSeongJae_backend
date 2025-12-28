package com.wirebarly.customer.model;

import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.CustomerErrorInfo;
import lombok.Getter;

@Getter
public class CustomerId {
    private final long value;

    public CustomerId(Long input) {
        validate(input);
        this.value = input;
    }

    private void validate(Long input) {
        if (input == null)
            throw new DomainException(CustomerErrorInfo.ID_NOT_EXIST);
        if (input <= 0) {
            throw new DomainException(CustomerErrorInfo.ID_NOT_POSITIVE);
        }
    }
}

