package com.wirebarley.error.info;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorInfo implements ErrorInfo {

    // Money
    Money_NOT_EXIST(ErrorCategory.INVALID_VALUE, "Money_NOT_EXIST", "돈의 값이 비어있을 수 없습니다."),
    Money_NOT_POSITIVE(ErrorCategory.INVALID_VALUE, "Money_NOT_POSITIVE", "돈의 값은 0 또는 양의 정수여야 합니다.");

    private final ErrorCategory category;
    private final String code;
    private final String message;
}
