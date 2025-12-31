package com.wirebarly.account.model;

import com.wirebarly.error.exception.DomainException;
import com.wirebarly.utils.MyStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class BankCodeTest {
    private static MockedStatic<MyStringUtils> myStringUtils;

    @BeforeEach
    void init() {
        myStringUtils = mockStatic(MyStringUtils.class);
    }

    @AfterEach
    void close() {
        myStringUtils.close();
    }

    @DisplayName("유효한 은행코드이면 알맞은 은행 Enum이 반환된다.")
    @ParameterizedTest
    @CsvSource({
            "039, KYONGNAM",
            "003, IBK",
    })
    void from(String code, String expected) {
        // given
        given(MyStringUtils.isPositiveNumber(any()))
                .willReturn(true);

        // when
        BankCode result = BankCode.from(code);

        // then
        assertThat(result.name()).isEqualTo(expected);
    }

    @DisplayName("매칭되는 은행이 없으면 예외를 던진다.")
    @Test
    void fromWhenIsNotMatch() {
        // given
        String code = "0000009";
        given(MyStringUtils.isPositiveNumber(any()))
                .willReturn(true);

        // when // then
        assertThatThrownBy(() -> BankCode.from(code))
                .isInstanceOf(DomainException.class)
                .hasMessage("유효하지 않은 은행코드입니다.");
    }
}
