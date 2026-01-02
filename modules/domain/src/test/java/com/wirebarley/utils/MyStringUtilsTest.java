package com.wirebarley.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class MyStringUtilsTest {

    @DisplayName("양의 정수인 경우에만 true를 반환하다.")
    @ParameterizedTest
    @CsvSource({
            "1, true",
            "0000009, true",
            "0, false",
            "-1, false",
            "1.2, false",
    })
    void isPositiveNumber(String value, boolean expected) {
        // when
        boolean result = MyStringUtils.isPositiveNumber(value);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("Null이거나 값이 비어있으면 true, 아니면 false를 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {
            "null, true",
            "'', true",
            "' ', true",
            "a, false",},
            nullValues = {"null"}
    )
    void isEmpty(String value, boolean expected) {
        // when
        boolean result = MyStringUtils.isEmpty(value);

        // then
        assertThat(result).isEqualTo(expected);
    }
}
