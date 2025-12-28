package com.wirebarly.error.info;

import com.wirebarly.error.info.ErrorCategory;
import com.wirebarly.error.info.ErrorInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorInfo implements ErrorInfo {
    INTERNAL_SERVER_ERROR(ErrorCategory.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버에서 알 수 없는 오류가 발생했습니다.");

    private final ErrorCategory category;
    private final String code;
    private final String message;
}
