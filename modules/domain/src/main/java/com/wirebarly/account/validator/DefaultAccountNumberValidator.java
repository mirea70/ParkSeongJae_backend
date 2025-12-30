package com.wirebarly.account.validator;

import com.wirebarly.account.model.AccountNumber;
import com.wirebarly.account.policy.AccountPolicy;
import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.AccountErrorInfo;

public class DefaultAccountNumberValidator implements AccountNumberValidator {

    @Override
    public void validate(AccountNumber accountNumber) {
        String rawValue = accountNumber.getValue();

        if (rawValue.length() < AccountPolicy.accountNumberMinLength || rawValue.length() > AccountPolicy.accountNumberMaxLength)
            throw new DomainException(AccountErrorInfo.INVALID_NUMBER_SIZE);
        if (isAllZero(rawValue))
            throw new DomainException(AccountErrorInfo.NUMBER_NOT_ALL_ZERO);
    }

    private boolean isAllZero(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != '0') return false;
        }
        return true;
    }
}
