package com.wirebarly.in.web.account.controller;

import com.wirebarly.in.account.usecase.AccountUseCase;
import com.wirebarly.in.transfer.usecase.TransferUseCase;
import com.wirebarly.in.web.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerGetTransfersTest extends ControllerTestSupport {

    @MockitoBean
    private AccountUseCase accountUseCase;

    @MockitoBean
    private TransferUseCase transferUseCase;

    @DisplayName("송금/수취내역 조회 성공케이스")
    @Test
    void getTransfers() throws Exception {
        // given
        Long accountId = 1L;
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.util.List<com.wirebarly.in.transfer.result.TransferResult> responses = java.util.List.of(
                new com.wirebarly.in.transfer.result.TransferResult(
                        1L, "WITHDRAW", 2L, 1000L, 0L, now),
                new com.wirebarly.in.transfer.result.TransferResult(
                        2L, "DEPOSIT", 3L, 2000L, 0L, now.plusMinutes(1)));

        given(transferUseCase.getTransfers(accountId)).willReturn(responses);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/accounts/{accountId}/transfers", accountId));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].transferId").value(1L))
                .andExpect(jsonPath("$[0].direction").value("WITHDRAW"))
                .andExpect(jsonPath("$[0].counterpartyAccountId").value(2L))
                .andExpect(jsonPath("$[0].amount").value(1000L))
                .andExpect(jsonPath("$[0].fee").value(0L))
                .andExpect(jsonPath("$[0].transferredAt").exists())
                .andExpect(jsonPath("$[1].transferId").value(2L))
                .andExpect(jsonPath("$[1].direction").value("DEPOSIT"));
    }

    @DisplayName("송금/수취내역 조회 시, 계좌 ID가 숫자가 아닌 값이 입력되면 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = { "abc" })
    void getTransfersAccountIdNotNumber(String accountId) throws Exception {
        // when
        ResultActions result = mockMvc.perform(get("/api/v1/accounts/{accountId}/transfers", accountId));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"));
    }
}
