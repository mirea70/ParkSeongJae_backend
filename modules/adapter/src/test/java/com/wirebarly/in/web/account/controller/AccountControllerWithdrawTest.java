package com.wirebarly.in.web.account.controller;

import com.wirebarly.in.account.usecase.AccountUseCase;
import com.wirebarly.in.transfer.usecase.TransferUseCase;
import com.wirebarly.in.web.ControllerTestSupport;
import com.wirebarly.in.web.account.request.AccountWithdrawRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerWithdrawTest extends ControllerTestSupport {

    @MockitoBean
    private AccountUseCase accountUseCase;

    @MockitoBean
    private TransferUseCase transferUseCase;

    @DisplayName("출금 성공케이스")
    @Test
    void withdraw() throws Exception {
        // given
        Long accountId = 1L;
        AccountWithdrawRequest request = new AccountWithdrawRequest(1000L);

        // when
        ResultActions result = postRequest("/api/v1/accounts/{accountId}/withdraw", accountId, request);

        // then
        result.andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("출금 요청 시, 계좌 ID가 숫자가 아닌 값이 입력되면 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = { "abc" })
    void withdrawWhenAccountIdNotNumber(String accountId) throws Exception {
        // given
        AccountWithdrawRequest request = new AccountWithdrawRequest(3000L);

        // when
        ResultActions result = postRequest("/api/v1/accounts/{accountId}/withdraw", accountId, request);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"));
    }

    @DisplayName("출금 요청 시, 입금금액은 양의 정수여야한다.")
    @ParameterizedTest
    @ValueSource(longs = { -1000L, 0 })
    @NullSource
    void withdrawAmountCase(Long amount) throws Exception {
        // given
        AccountWithdrawRequest request = new AccountWithdrawRequest(amount);

        // when
        ResultActions result = postRequest("/api/v1/accounts/{accountId}/withdraw", 1L, request);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"));
    }
}
