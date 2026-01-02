package com.wirebarley.error.info;

public interface ErrorInfo {
    ErrorCategory getCategory();
    String getCode();
    String getMessage();
}
