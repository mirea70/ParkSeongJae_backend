package com.wirebarly.error.info;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountTransactionErrorInfo implements ErrorInfo {

    // AccountTransactionId
    ID_NOT_EXIST(ErrorCategory.INVALID_VALUE, "ACCOUNT_TRANSACTION_ID_NOT_EXIST", "계좌 거래의 시스템ID 값이 비어있을 수 없습니다."),
    ID_NOT_POSITIVE(ErrorCategory.INVALID_VALUE, "ACCOUNT_TRANSACTION_ID_NOT_POSITIVE", "계좌 거래의 시스템ID 값은 양의 정수여야 합니다."),

    // AccountTransactionType
    INVALID_TYPE(ErrorCategory.INVALID_VALUE, "INVALID_ACCOUNT_TRANSACTION_TYPE", "유효하지 않은 계좌 거래 타입입니다."),

    // AccountTransactionTransferType
    INVALID_TRANSFER_TYPE(ErrorCategory.INVALID_VALUE, "INVALID_ACCOUNT_TRANSACTION_TRANSFER_Type", "유효하지 않은 거래 송금 타입입니다.");

    private final ErrorCategory category;
    private final String code;
    private final String message;
}
