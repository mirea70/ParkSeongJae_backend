package com.wirebarly.account.validator;

import com.wirebarly.account.model.AccountNumber;
import com.wirebarly.account.model.BankCode;

import java.util.EnumMap;
import java.util.Map;

public class AccountNumberValidateExecutor {
    private static final Map<BankCode, AccountNumberValidator> validators = initValidators();
    private static final DefaultAccountNumberValidator defaultValidator = new DefaultAccountNumberValidator();

    private AccountNumberValidateExecutor() {}

    private static Map<BankCode, AccountNumberValidator> initValidators() {
        // 은행별 계좌번호 규칙 추가 시, 구현체 추가

        return new EnumMap<>(BankCode.class);
    }

    public static void execute(BankCode bankCode, AccountNumber accountNumber) {
        AccountNumberValidator validator = validators.getOrDefault(bankCode, defaultValidator);
        validator.validate(accountNumber);
    }
}
