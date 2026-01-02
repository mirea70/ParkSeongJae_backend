package com.wirebarly.error.info;

import com.wirebarly.transfer.policy.TransferPolicy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransferErrorInfo implements ErrorInfo {

    // TransferId
    ID_NOT_EXIST(ErrorCategory.INVALID_VALUE, "TransferId_NOT_EXIST", "송금 시스템ID 값이 비어있을 수 없습니다."),
    ID_NOT_POSITIVE(ErrorCategory.INVALID_VALUE, "TransferId_NOT_POSITIVE", "송금 시스템ID 값은 양의 정수여야 합니다."),

    // Amount
    OVER_TRANSFER_LIMIT(ErrorCategory.INVALID_VALUE, "OVER_TRANSFER_LIMIT", "송금할 수 있는 일일 한도를 초과하였습니다."),
    TOO_SMALL_TRANSFER_AMOUNT(ErrorCategory.INVALID_VALUE, "TOO_SMALL_TRANSFER_AMOUNT", "송금액은 " + TransferPolicy.TRANSFER_MIN_AMOUNT + " 이상만 가능합니다.");

    private final ErrorCategory category;
    private final String code;
    private final String message;

}
