package com.wirebarley.account.model;

import com.wirebarley.error.exception.DomainException;
import com.wirebarley.error.info.AccountErrorInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AccountIdTest {

    @DisplayName("객체 생성 시, 계좌의 Key 값이 존재하고 양수이면 잘 생성된다.")
    @Test
    void factory() {
        // given
        Long input = 2L;

        // when
        AccountId result = new AccountId(input);

        // then
        long expected = 2L;

        assertThat(result.getValue()).isEqualTo(expected);
    }

    @DisplayName("유효하지 않은 값이 들어오면 예외를 던진다.")
    @ParameterizedTest
    @CsvSource(value = {
            "null, ID_NOT_EXIST",
            "0, ID_NOT_POSITIVE",
            "-1, ID_NOT_POSITIVE",
    }, nullValues = { "null" })
    void factoryWithLongWhenInvalid(Long input, AccountErrorInfo errorInfo) {
        // when // then
        assertThatThrownBy(() -> new AccountId(input))
                .isInstanceOf(DomainException.class)
                .extracting("errorInfo")
                .isEqualTo(errorInfo);
    }
}
