package com.wirebarly.common;

import com.wirebarly.common.model.Money;
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
class MoneyTest {

    @DisplayName("현재잔액의 값 생성 시, 존재하고 0이상의 정수이면 잘 생성된다.")
    @Test
    void factory() {
        // given
        Long input = 2000L;

        // when
        Money result = new Money(input);

        // then
        long expected = 2000L;

        assertThat(result.getValue()).isEqualTo(expected);
    }

    @DisplayName("현재잔액의 값 생성 시, 유효하지 않으면 예외를 던진다.")
    @ParameterizedTest
    @CsvSource(value = {
            "null, 계좌의 현재잔액 값이 비어있을 수 없습니다.",
            "-1000, 계좌의 현재잔액 값은 0 또는 양의 정수여야 합니다.",
    }, nullValues = {"null"})
    void factoryWhenInvalid(Long input, String errorMessage) {
        // when // then
        assertThatThrownBy(() -> new Money(input))
                .isInstanceOf(DomainException.class)
                .hasMessage(errorMessage);
    }
}
