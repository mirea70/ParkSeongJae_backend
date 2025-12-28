package com.wirebarly.customer;

import com.wirebarly.customer.model.CustomerId;
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
class CustomerIdTest {

    @DisplayName("객체 생성 시, 고객의 Key 값이 유효하다면 잘 생성된다.")
    @Test
    void factoryWithString() {
        // given
        Long input = 2L;

        // when
        CustomerId result = new CustomerId(input);

        // then
        long expected = 2L;

        assertThat(result.getValue()).isEqualTo(expected);
    }

    @DisplayName("유효하지 않은 값이 들어오면 예외를 던진다.")
    @ParameterizedTest
    @CsvSource(value = {
            "null, 고객의 시스템ID 값이 비어있을 수 없습니다.",
            "0, 고객의 시스템ID 값은 양의 정수여야 합니다.",
            "-1, 고객의 시스템ID 값은 양의 정수여야 합니다.",
    }, nullValues = {"null"})
    void factoryWithLongWhenInvalid(Long input, String errorMessage) {
        // when // then
        assertThatThrownBy(() -> new CustomerId(input))
                .isInstanceOf(DomainException.class)
                .hasMessage(errorMessage);
    }
}
