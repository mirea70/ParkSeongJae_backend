package com.wirebarley.in.web.error.handler;

import com.wirebarley.error.exception.BusinessException;
import com.wirebarley.error.exception.DomainException;
import com.wirebarley.error.info.SystemErrorInfo;
import com.wirebarley.error.info.ErrorInfo;
import com.wirebarley.in.web.error.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.Objects;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException e, HttpServletRequest request) {
        ErrorInfo errorInfo = e.getErrorInfo();
        return ResponseEntity
                .status(HttpStatusErrorMapper.map(errorInfo.getCategory()))
                .body(
                        ErrorResponse.of(errorInfo, request.getRequestURI(), e.getDetails())
                );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        ErrorInfo errorInfo = e.getErrorInfo();
        return ResponseEntity
                .status(HttpStatusErrorMapper.map(errorInfo.getCategory()))
                .body(
                        ErrorResponse.of(errorInfo, request.getRequestURI(), e.getDetails())
                );
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .sorted((a, b) -> Integer.compare(priority(a), priority(b)))
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("요청 값이 올바르지 않습니다.");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.of(
                                "INVALID_INPUT_VALUE",
                                message,
                                request.getRequestURI(),
                                Map.of()
                        )
                );
    }

    private int priority(org.springframework.validation.FieldError fe) {
        String code = fe.getCode();
        if (code == null) return 100;

        return switch (code) {
            case "NotBlank", "NotNull", "NotEmpty" -> 0;
            case "Pattern" -> 1;
            case "Size" -> 2;
            default -> 50;
        };
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request
    ) {
        String message = String.format(
                "%s 값은 올바른 형식이 아닙니다.",
                e.getName() // accountId
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.of(
                                "INVALID_INPUT_VALUE",
                                message,
                                request.getRequestURI(),
                                Map.of()
                        )
                );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ErrorResponse.of("NOT_FOUND", e.getMessage(), request.getRequestURI(), Map.of())
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception e, HttpServletRequest request) {
        ErrorInfo errorInfo = SystemErrorInfo.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(HttpStatusErrorMapper.map(errorInfo.getCategory()))
                .body(
                        ErrorResponse.of(errorInfo, request.getRequestURI(), Map.of())
                );
    }
}
