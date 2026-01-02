package com.wirebarley.account.validator;

import com.wirebarley.account.model.AccountNumber;
import com.wirebarley.account.policy.AccountPolicy;
import com.wirebarley.error.exception.DomainException;
import com.wirebarley.error.info.AccountErrorInfo;

public class DefaultAccountNumberValidator implements AccountNumberValidator {

    @Override
    public void validate(AccountNumber accountNumber) {
        String rawValue = accountNumber.getValue();

        if (rawValue.length() < AccountPolicy.ACCOUNT_NUMBER_MIN_LEN || rawValue.length() > AccountPolicy.ACCOUNT_NUMBER_MAX_LEN)
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
