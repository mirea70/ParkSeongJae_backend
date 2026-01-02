package com.wirebarley.account.model;

import com.wirebarley.error.exception.DomainException;
import com.wirebarley.error.info.AccountErrorInfo;
import com.wirebarley.utils.MyStringUtils;
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
