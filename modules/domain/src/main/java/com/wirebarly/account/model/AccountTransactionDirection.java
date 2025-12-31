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
public enum AccountTransactionDirection {
    IN("안으로"),
    OUT("밖으로");

    private final String description;

    private static final Map<String, AccountTransactionDirection> valueMap =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            direction -> direction.name().toLowerCase(),
                            direction -> direction
                    ));

    static AccountTransactionDirection from(String input) {
        if (input == null) {
            throw new DomainException(AccountTransactionErrorInfo.INVALID_DIRECTION);
        }

        AccountTransactionDirection result = valueMap.get(input.toLowerCase());
        if (result == null) {
            throw new DomainException(AccountTransactionErrorInfo.INVALID_DIRECTION);
        }

        return result;
    }
}
