package com.wirebarly.account.model;

import com.wirebarly.error.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AccountTransactionIdTest {
    @DisplayName("객체 생성 시, 계좌의 Key 값이 존재하고 양수이면 잘 생성된다.")
    @Test
    void factory() {
        // given
        Long input = 2L;

        // when
        AccountTransactionId result = new AccountTransactionId(input);

        // then
        long expected = 2L;

        assertThat(result.getValue()).isEqualTo(expected);
    }

    @DisplayName("객체 생성 시, 유효하지 않은 값이 들어오면 예외를 던진다.")
    @ParameterizedTest
    @CsvSource(value = {
            "null, 계좌 거래의 시스템ID 값이 비어있을 수 없습니다.",
            "0, 계좌 거래의 시스템ID 값은 양의 정수여야 합니다.",
            "-1, 계좌 거래의 시스템ID 값은 양의 정수여야 합니다.",
    }, nullValues = {"null"})
    void factoryWhenInvalid(Long input, String errorMessage) {
        // when // then
        assertThatThrownBy(() -> new AccountTransactionId(input))
                .isInstanceOf(DomainException.class)
                .hasMessage(errorMessage);
    }
}