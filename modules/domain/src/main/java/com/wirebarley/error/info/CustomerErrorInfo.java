package com.wirebarley.error.info;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomerErrorInfo implements ErrorInfo {
    // Customer
    NOT_FOUND(ErrorCategory.NOT_FOUND, "CUSTOMER_NOT_FOUND", "고객을 찾을 수 없습니다."),

    // CustomerId
    ID_NOT_EXIST(ErrorCategory.INVALID_VALUE, "CUSTOMER_ID_NOT_EXIST", "고객의 시스템ID 값이 비어있을 수 없습니다."),
    ID_NOT_POSITIVE(ErrorCategory.INVALID_VALUE, "CUSTOMER_ID_NOT_POSITIVE", "고객의 시스템ID 값은 양의 정수여야 합니다.");

    private final ErrorCategory category;
    private final String code;
    private final String message;
}
