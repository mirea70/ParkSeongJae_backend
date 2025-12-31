package com.wirebarly.account.model;

import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.AccountTransactionErrorInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum AccountTransactionType {
    DEPOSIT("입금"),
    WITHDRAW("출금");

    private final String description;

    private static final Map<String, AccountTransactionType> valueMap =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            type -> type.name().toLowerCase(),
                            type -> type
                    ));
    static AccountTransactionType from(String input) {
        if (input == null) {
            throw new DomainException(AccountTransactionErrorInfo.INVALID_TYPE);
        }

        AccountTransactionType result = valueMap.get(input.toLowerCase());
        if (result == null) {
            throw new DomainException(AccountTransactionErrorInfo.INVALID_TYPE);
        }

        return result;
    }
}
