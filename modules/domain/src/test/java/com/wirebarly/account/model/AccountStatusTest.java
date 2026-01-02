package com.wirebarly.account.model;

import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.AccountErrorInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AccountStatusTest {

    @DisplayName("입력 문자열에 매칭되는 계좌 상태의 Enum을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = { "ACTIVE", "active" })
    void from(String status) {
        // when
        AccountStatus result = AccountStatus.from(status);

        // then
        assertThat(result).isEqualTo(AccountStatus.ACTIVE);
    }

    @DisplayName("입력 문자열이 유효하지 않으면 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(strings = { "가짜" })
    @NullSource
    void fromWhenInvalid(String input) {
        // when // then
        assertThatThrownBy(() -> AccountStatus.from(input))
                .isInstanceOf(DomainException.class)
                .extracting("errorInfo")
                .isEqualTo(AccountErrorInfo.INVALID_STATUS);
    }
}