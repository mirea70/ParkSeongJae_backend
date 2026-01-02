package com.wirebarly.in.web.account.controller;

import com.wirebarly.in.account.usecase.AccountUseCase;
import com.wirebarly.in.transfer.usecase.TransferUseCase;
import com.wirebarly.in.web.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerRemoveTest extends ControllerTestSupport {

    @MockitoBean
    private AccountUseCase accountUseCase;

    @MockitoBean
    private TransferUseCase transferUseCase;

    @DisplayName("계좌 삭제 성공케이스")
    @Test
    void deleteAccount() throws Exception {
        // given
        Long accountId = 1L;

        // when
        ResultActions result = mockMvc.perform(delete("/api/v1/accounts/{accountId}", accountId));

        // then
        result.andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("삭제 요청 시, 계좌 ID가 숫자가 아닌 값이 입력되면 실패한다.")
    @Test
    void deleteAccountWhenAccountIdNotNumber() throws Exception {
        // given
        String accountId = "abc";

        // when
        ResultActions result = mockMvc.perform(
                delete("/api/v1/accounts/{accountId}", accountId));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"));
    }
}
