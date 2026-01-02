package com.wirebarley.error.info;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SystemErrorInfo implements ErrorInfo {
    INTERNAL_SERVER_ERROR(ErrorCategory.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버에서 알 수 없는 오류가 발생했습니다."),
    TRANSACTION_PROGRESS_ERROR(ErrorCategory.INTERNAL_SERVER_ERROR, "TRANSACTION_PROGRESS_ERROR", "트랜잭션 처리 중 문제가 발생했습니다.");

    private final ErrorCategory category;
    private final String code;
    private final String message;
}
