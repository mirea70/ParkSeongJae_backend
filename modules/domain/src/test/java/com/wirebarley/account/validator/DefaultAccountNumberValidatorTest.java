package com.wirebarley.account.validator;

import com.wirebarley.account.model.AccountNumber;
import com.wirebarley.account.model.AccountNumberFactory;
import com.wirebarley.error.exception.DomainException;
import com.wirebarley.error.info.AccountErrorInfo;
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
        void validate(AccountNumber accountNumber, AccountErrorInfo errorInfo) {
                // when // then
                assertThatThrownBy(() -> defaultAccountNumberValidator.validate(accountNumber))
                                .isInstanceOf(DomainException.class)
                                .extracting("errorInfo")
                                .isEqualTo(errorInfo);
        }

        static Stream<Arguments> accountNumberCases() {
                return Stream.of(
                                Arguments.of(
                                                AccountNumberFactory.of("1324"),
                                                AccountErrorInfo.INVALID_NUMBER_SIZE),
                                Arguments.of(
                                                AccountNumberFactory.of("1324333333333333333333333333333333"),
                                                AccountErrorInfo.INVALID_NUMBER_SIZE),
                                Arguments.of(
                                                AccountNumberFactory.of("0000000000000"),
                                                AccountErrorInfo.NUMBER_NOT_ALL_ZERO));
        }
}