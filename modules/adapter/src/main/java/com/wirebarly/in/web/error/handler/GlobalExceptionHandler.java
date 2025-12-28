package com.wirebarly.in.web.error.handler;

import com.wirebarly.error.exception.BusinessException;
import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.CommonErrorInfo;
import com.wirebarly.error.info.ErrorInfo;
import com.wirebarly.in.web.error.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;


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
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.of(
                                "INVALID_INPUT_VALUE",
                                e.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                                request.getRequestURI(),
                                Map.of()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception e, HttpServletRequest request) {
        ErrorInfo errorInfo = CommonErrorInfo.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(HttpStatusErrorMapper.map(errorInfo.getCategory()))
                .body(
                        ErrorResponse.of(errorInfo, request.getRequestURI(), Map.of())
                );
    }
}
