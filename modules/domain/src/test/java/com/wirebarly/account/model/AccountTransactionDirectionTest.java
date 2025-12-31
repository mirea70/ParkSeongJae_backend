package com.wirebarly.account.model;

import com.wirebarly.error.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AccountTransactionDirectionTest {

    @DisplayName("입력 문자열에 매칭되는 Direction의 Enum을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"IN", "in"})
    void from(String direction) {
        // when
        AccountTransactionDirection result = AccountTransactionDirection.from(direction);

        // then
        assertThat(result).isEqualTo(AccountTransactionDirection.IN);
    }

    @DisplayName("입력 문자열이 유효하지 않으면 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(strings = {"가짜"})
    @NullSource
    void fromWhenInvalid(String input) {
        // when // then
        assertThatThrownBy(() -> AccountTransactionDirection.from(input))
                .isInstanceOf(DomainException.class)
                .hasMessage("유효하지 않은 계좌 거래 방향입니다.");
    }
}