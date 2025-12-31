package com.wirebarly.account.model;

import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.AccountErrorInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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

    private static final Map<String, BankCode> valueMap =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            bankCode -> bankCode.code,
                            bankCode -> bankCode
                    ));
    static BankCode from(String input) {
        if (input == null) {
            throw new DomainException(AccountErrorInfo.INVALID_BANK_CODE);
        }

        BankCode result = valueMap.get(input.toLowerCase());
        if (result == null) {
            throw new DomainException(AccountErrorInfo.INVALID_BANK_CODE);
        }

        return result;
    }
}
