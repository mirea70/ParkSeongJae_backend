package com.wirebarly.common.model;

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

    @DisplayName("돈의 값이 값 객체로 잘 담겨 생성된다.")
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

    @DisplayName("돈의 값이, 유효하지 않으면 예외를 던진다.")
    @ParameterizedTest
    @CsvSource(value = {
            "null, 돈의 값이 비어있을 수 없습니다.",
            "-1000, 돈의 값은 0 또는 양의 정수여야 합니다.",
    }, nullValues = {"null"})
    void validate(Long input, String errorMessage) {
        // when // then
        assertThatThrownBy(() -> Money.validate(input))
                .isInstanceOf(DomainException.class)
                .hasMessage(errorMessage);
    }

    @DisplayName("돈의 증가는 새로운 값 객체에 더해져 반환된다.")
    @Test
    void plus() {
        // given
        Long beforeValue = 1000L;
        Money before = new Money(beforeValue);
        Long amount = 2000L;
        Money amountMoney = new Money(amount);

        // when
        Money after = before.plus(amountMoney);

        // then
        assertThat(after.getValue()).isEqualTo(beforeValue + amount);
    }

    @DisplayName("돈의 감소는 새로운 값 객체에 감하여 반환된다.")
    @Test
    void minus() {
        // given
        Long beforeValue = 1000L;
        Money before = new Money(beforeValue);
        Long amount = 2000L;
        Money amountMoney = new Money(amount);

        // when
        Money after = before.minus(amountMoney);

        // then
        assertThat(after.getValue()).isEqualTo(beforeValue - amount);
    }

    @DisplayName("대상보다 크면 true, 아니면 false를 반환한다.")
    @Test
    void isGreaterThan() {
        // given
        Money money = new Money(2000L);
        Money target1 = new Money(1000L);
        Money target2 = new Money(2000L);
        Money target3 = new Money(3000L);

        // when
        boolean result1 = money.isGreaterThan(target1);
        boolean result2 = money.isGreaterThan(target2);
        boolean result3 = money.isGreaterThan(target3);

        // then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
        assertThat(result3).isFalse();
    }
}
