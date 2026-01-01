package com.wirebarly.transfer.model;

import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.TransferErrorInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TransferIdTest {
    @DisplayName("객체 생성 시, 계좌의 Key 값이 존재하고 양수이면 잘 생성된다.")
    @Test
    void factory() {
        // given
        Long input = 2L;

        // when
        TransferId result = new TransferId(input);

        // then
        long expected = 2L;

        assertThat(result.getValue()).isEqualTo(expected);
    }

    @DisplayName("객체 생성 시, 유효하지 않은 값이 들어오면 예외를 던진다.")
    @ParameterizedTest
    @MethodSource("transferIdInvalidCases")
    void factoryWhenInvalid(Long input, TransferErrorInfo errorInfo) {
        // when // then
        assertThatThrownBy(() -> new TransferId(input))
                .isInstanceOf(DomainException.class)
                .extracting("errorInfo")
                .isEqualTo(errorInfo);
    }

    private static Stream<Arguments> transferIdInvalidCases() {
        return Stream.of(
                Arguments.of(
                        null,
                        TransferErrorInfo.ID_NOT_EXIST
                ),
                Arguments.of(
                        0L,
                        TransferErrorInfo.ID_NOT_POSITIVE
                ),
                Arguments.of(
                        -1L,
                        TransferErrorInfo.ID_NOT_POSITIVE
                )
        );
    }
}