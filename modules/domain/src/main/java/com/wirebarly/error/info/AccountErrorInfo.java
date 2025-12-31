package com.wirebarly.error.info;

import com.wirebarly.account.policy.AccountPolicy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountErrorInfo implements ErrorInfo {

    // Account
    NOT_FOUND(ErrorCategory.NOT_FOUND, "ACCOUNT_NOT_FOUND", "계좌를 찾을 수 없습니다."),
    CLOSED(ErrorCategory.INVALID_VALUE, "ACCOUNT_CLOSED", "해지된 계좌에는 요청이 불가능합니다."),

    // AccountId
    ID_NOT_EXIST(ErrorCategory.INVALID_VALUE, "ACCOUNT_ID_NOT_EXIST", "계좌의 시스템ID 값이 비어있을 수 없습니다."),
    ID_NOT_POSITIVE(ErrorCategory.INVALID_VALUE, "ACCOUNT_ID_NOT_POSITIVE", "계좌의 시스템ID 값은 양의 정수여야 합니다."),

    // BankCode
    INVALID_BANK_CODE(ErrorCategory.INVALID_VALUE, "INVALID_BANK_CODE", "유효하지 않은 은행코드입니다."),

    // AccountNumber
    NUMBER_NOT_EXIST(ErrorCategory.INVALID_VALUE, "ACCOUNT_NUMBER_NOT_EXIST", "계좌번호의 값이 비어있을 수 없습니다."),
    INVALID_NUMBER_SIZE(ErrorCategory.INVALID_VALUE, "INVALID_ACCOUNT_NUMBER_SIZE",
            "계좌번호는 길이가 " + AccountPolicy.ACCOUNT_NUMBER_MIN_LEN + " 이상, " + AccountPolicy.ACCOUNT_NUMBER_MAX_LEN + " 이하여야 합니다."),
    NUMBER_NOT_ALL_ZERO(ErrorCategory.INVALID_VALUE, "ACCOUNT_NUMBER_NOT_ALL_ZERO", "계좌번호의 값이 모두 0일 경우는 불가능합니다."),

    // BALANCE
    LACK_BALANCE(ErrorCategory.INVALID_VALUE, "LACK_BALANCE", "잔액이 부족합니다."),

    // DEPOSIT
    DEPOSIT_NOT_EXIST(ErrorCategory.INVALID_VALUE, "DEPOSIT_NOT_EXIST", "계좌에 입금할 값이 비어있을 수 없습니다."),
    DEPOSIT_NOT_POSITIVE(ErrorCategory.INVALID_VALUE, "DEPOSIT_NOT_POSITIVE", "계좌에 입금할 값은 양의 정수여야 합니다."),

    // WITHDRAW
    WITHDRAW_NOT_EXIST(ErrorCategory.INVALID_VALUE, "WITHDRAW_NOT_EXIST", "계좌에 출금할 값이 비어있을 수 없습니다."),
    WITHDRAW_NOT_POSITIVE(ErrorCategory.INVALID_VALUE, "WITHDRAW_NOT_POSITIVE", "계좌에 출금할 값은 양의 정수여야 합니다."),
    OVER_WITHDRAW_LIMIT(ErrorCategory.INVALID_VALUE, "OVER_WITHDRAW_LIMIT", "일일 출금한도액인 " + AccountPolicy.ACCOUNT_WITHDRAW_DAILY_LIMIT + "를 초과하였습니다."),

    // AccountStatus
    INVALID_STATUS(ErrorCategory.INVALID_VALUE, "INVALID_ACCOUNT_STATUS", "유효하지 않은 계좌 상태입니다.");

    private final ErrorCategory category;
    private final String code;
    private final String message;
}
