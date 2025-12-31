package com.wirebarly.error.info;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransferErrorInfo implements ErrorInfo {

    // TransferId
    ID_NOT_EXIST(ErrorCategory.INVALID_VALUE, "TransferId_NOT_EXIST", "이체의 시스템ID 값이 비어있을 수 없습니다."),
    ID_NOT_POSITIVE(ErrorCategory.INVALID_VALUE, "TransferId_NOT_POSITIVE", "이체의 시스템ID 값은 양의 정수여야 합니다.");

    private final ErrorCategory category;
    private final String code;
    private final String message;

}
