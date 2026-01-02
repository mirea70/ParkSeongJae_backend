package com.wirebarley.in.web.account.controller;

import com.wirebarley.in.account.usecase.AccountUseCase;
import com.wirebarley.in.transfer.usecase.TransferUseCase;
import com.wirebarley.in.web.ControllerTestSupport;
import com.wirebarley.in.web.account.request.TransferCreateRequest;
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

class AccountControllerTransferTest extends ControllerTestSupport {

    @MockitoBean
    private AccountUseCase accountUseCase;

    @MockitoBean
    private TransferUseCase transferUseCase;

    @DisplayName("송금 성공케이스")
    @Test
    void transfer() throws Exception {
        // given
        Long accountId = 1L;
        TransferCreateRequest request = new TransferCreateRequest(2L, 1000L);

        // when
        ResultActions result = postRequest("/api/v1/accounts/{accountId}/transfer", accountId, request);

        // then
        result.andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("송금 요청 시, 계좌 ID가 숫자가 아닌 값이 입력되면 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = { "abc" })
    void transferWhenAccountIdNotNumber(String accountId) throws Exception {
        // given
        TransferCreateRequest request = new TransferCreateRequest(3L, 5000L);

        // when
        ResultActions result = postRequest("/api/v1/accounts/{accountId}/transfer", accountId, request);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"));
    }

    @DisplayName("송금 요청 시, 수취자 계좌의 시스템 ID는 양의 정수여야한다.")
    @ParameterizedTest
    @ValueSource(longs = { -10L, 0 })
    @NullSource
    void transferToAccountIdCase(Long toAccountId) throws Exception {
        // given
        TransferCreateRequest request = new TransferCreateRequest(toAccountId, 3000L);

        // when
        ResultActions result = postRequest("/api/v1/accounts/{accountId}/transfer", 1L, request);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"));
    }

    @DisplayName("송금 요청 시, 송금액은 양의 정수여야한다.")
    @ParameterizedTest
    @ValueSource(longs = { -1000L, 0 })
    @NullSource
    void transferAmountCase(Long amount) throws Exception {
        // given
        TransferCreateRequest request = new TransferCreateRequest(3L, amount);

        // when
        ResultActions result = postRequest("/api/v1/accounts/{accountId}/transfer", 1L, request);

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"));
    }
}
