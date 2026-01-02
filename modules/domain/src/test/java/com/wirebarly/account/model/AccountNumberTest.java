package com.wirebarly.account.model;

import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.AccountErrorInfo;
import com.wirebarly.utils.MyStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class AccountNumberTest {
    private static MockedStatic<MyStringUtils> myStringUtils;

    @BeforeEach
    void init() {
        myStringUtils = mockStatic(MyStringUtils.class);
    }

    @AfterEach
    void close() {
        myStringUtils.close();
    }

    @DisplayName("객체 생성 시, '-'는 제외하고 값이 저장된다.")
    @Test
    void factory() {
        // given
        given(MyStringUtils.isEmpty(any()))
                .willReturn(false);
        String input = "132-1134-5678";

        // when
        AccountNumber result = new AccountNumber(input);

        // then
        String expected = "13211345678";
        assertThat(result.getValue()).isEqualTo(expected);
    }

    @DisplayName("객체 생성 시, 계좌번호의 값이 될 입력값이 값이 존재하지않으면 예외를 던진다.")
    @Test
    void factoryWhenIsEmpty() {
        // given
        given(MyStringUtils.isEmpty(any()))
                .willReturn(true);
        // when // then
        assertThatThrownBy(() -> new AccountNumber(null))
                .isInstanceOf(DomainException.class)
                .extracting("errorInfo")
                .isEqualTo(AccountErrorInfo.NUMBER_NOT_EXIST);
    }
}
