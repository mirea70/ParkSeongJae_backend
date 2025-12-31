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
public enum AccountTransactionTransferType {
    TRANSFER("송금"),
    TRANSFER_FEE("송금 수수료"),
    NONE("송금 외 거래");

    private final String description;

    private static final Map<String, AccountTransactionTransferType> valueMap =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            type -> type.name().toLowerCase(),
                            type -> type
                    ));
    static AccountTransactionTransferType from(String input) {
        if (input == null) {
            throw new DomainException(AccountTransactionErrorInfo.INVALID_TRANSFER_TYPE);
        }

        AccountTransactionTransferType result = valueMap.get(input.toLowerCase());
        if (result == null) {
            throw new DomainException(AccountTransactionErrorInfo.INVALID_TRANSFER_TYPE);
        }

        return result;
    }
}
