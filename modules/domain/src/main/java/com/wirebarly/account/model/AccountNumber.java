package com.wirebarly.account.model;

import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.AccountErrorInfo;
import com.wirebarly.utils.MyStringUtils;
import lombok.Getter;

@Getter
public class AccountNumber {
    private final String value;

    AccountNumber(String input) {
        validate(input);
        this.value = normalize(input);
    }
    private void validate(String input) {
        if (MyStringUtils.isEmpty(input))
            throw new DomainException(AccountErrorInfo.NUMBER_NOT_EXIST);
    }

    private String normalize(String input) {
        return input.replace("-", "").replace(" ", "");
    }
}
