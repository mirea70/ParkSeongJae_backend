package com.wirebarly.account.validator;

import com.wirebarly.account.model.AccountNumber;
import com.wirebarly.error.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultAccountNumberValidatorTest {
    private final DefaultAccountNumberValidator defaultAccountNumberValidator = new DefaultAccountNumberValidator();

    @DisplayName("계좌번호의 기본적인 유효성 검증 실패시 에러를 발생시킨다.")
    @ParameterizedTest
    @MethodSource("accountNumberCases")
    void validate(AccountNumber accountNumber, String errorMessage) {
        // when // then
        assertThatThrownBy(() -> defaultAccountNumberValidator.validate(accountNumber))
                .isInstanceOf(DomainException.class)
                .hasMessage(errorMessage);
    }

    static Stream<Arguments> accountNumberCases() {
        return Stream.of(
                Arguments.of(
                        new AccountNumber("1324"),
                        "계좌번호는 길이가 10 이상, 20 이하여야 합니다."
                ),
                Arguments.of(
                        new AccountNumber("1324333333333333333333333333333333"),
                        "계좌번호는 길이가 10 이상, 20 이하여야 합니다."
                ),
                Arguments.of(
                        new AccountNumber("0000000000000"),
                        "계좌번호의 값이 모두 0일 경우는 불가능합니다."
                )
        );
    }
}