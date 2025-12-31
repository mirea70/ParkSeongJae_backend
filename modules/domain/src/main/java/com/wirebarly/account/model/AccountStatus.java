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
public enum AccountStatus {
    ACTIVE("활성 상태"),
    CLOSED("해지 상태");

    private final String description;

    private static final Map<String, AccountStatus> valueMap =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            status -> status.name().toLowerCase(),
                            status -> status
                    ));

    public static AccountStatus from(String input) {
        if (input == null) {
            throw new DomainException(AccountErrorInfo.INVALID_STATUS);
        }

        AccountStatus result = valueMap.get(input.toLowerCase());
        if (result == null) {
            throw new DomainException(AccountErrorInfo.INVALID_STATUS);
        }

        return result;
    }
}
