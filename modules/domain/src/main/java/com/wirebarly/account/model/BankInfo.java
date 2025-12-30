package com.wirebarly.account.model;

import com.wirebarly.account.validator.AccountNumberValidateExecutor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BankInfo {
    private final BankCode bankCode;
    private final AccountNumber accountNumber;

    static BankInfo of(String bankCodeInput, String accountNumberInput) {
        BankCode bankCode = BankCode.from(bankCodeInput);
        AccountNumber accountNumber = new AccountNumber(accountNumberInput);
        AccountNumberValidateExecutor.execute(bankCode, accountNumber);

        return new BankInfo(bankCode, accountNumber);
    }
}
