package com.wirebarly.account.model;

import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.AccountTransactionErrorInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AccountTransactionTypeTest {

    @DisplayName("입력 문자열에 매칭되는 Type의 Enum을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = { "DEPOSIT", "deposit" })
    void from(String type) {
        // when
        AccountTransactionType result = AccountTransactionType.from(type);

        // then
        assertThat(result).isEqualTo(AccountTransactionType.DEPOSIT);
    }

    @DisplayName("입력 문자열이 유효하지 않으면 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(strings = { "가짜" })
    @NullSource
    void fromWhenInvalid(String input) {
        // when // then
        assertThatThrownBy(() -> AccountTransactionType.from(input))
                .isInstanceOf(DomainException.class)
                .extracting("errorInfo")
                .isEqualTo(AccountTransactionErrorInfo.INVALID_TYPE);
    }
}