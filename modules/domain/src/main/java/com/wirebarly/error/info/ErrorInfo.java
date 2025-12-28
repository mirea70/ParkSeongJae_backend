package com.wirebarly.error.info;

public interface ErrorInfo {
    ErrorCategory getCategory();
    String getCode();
    String getMessage();
}
