package com.wirebarly.account.model;

import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.AccountErrorInfo;
import com.wirebarly.utils.MyStringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BankCode {
    KYONGNAM("039", "경남은행"),
    GWANGJU("034", "광주은행"),
    NONGHYEOP("011", "NH농협은행"),
    BUSAN("032", "부산은행"),
    SHINHAN("088", "신한은행"),
    IBK("003", "IBK기업은행"),
    KOOKMIN("004", "KB국민은행");

    private final String code;
    private final String description;

    public static BankCode from(String code) {
        if (!MyStringUtils.isPositiveNumber(code)) {
            throw new DomainException(AccountErrorInfo.BANK_CODE_NOT_POSITIVE);
        }

        for (BankCode bankCode : BankCode.values()) {
            if (bankCode.code.equals(code)) {
                return bankCode;
            }
        }

        throw new DomainException(AccountErrorInfo.INVALID_BANK_CODE);
    }
}
