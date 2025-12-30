package com.wirebarly.in.web.account.controller;

import com.wirebarly.in.account.usecase.AccountUseCase;
import com.wirebarly.in.web.ControllerTestSupport;
import com.wirebarly.in.web.account.request.AccountCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerTest extends ControllerTestSupport {

    @MockitoBean
    private AccountUseCase accountUseCase;

    @DisplayName("계좌 등록 실패케이스")
    @ParameterizedTest
    @MethodSource("accountCreateRequestCases")
    void registerAccountWhenFail(AccountCreateRequest request, String errorMessage) throws Exception {
        // when // then
        mockMvc.perform(
                        post("/api/v1/accounts/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    static Stream<Arguments> accountCreateRequestCases() {
        return Stream.of(
                // customerId 실패케이스
                Arguments.of(
                        new AccountCreateRequest(
                                null,
                                "039",
                                "111111111111"
                        ),
                        "고객 System ID 값은 존재해야합니다."
                ),
                Arguments.of(
                        new AccountCreateRequest(
                                -1L,
                                "039",
                                "111111111111"
                        ),
                        "고객 System ID 값은 양의 정수여야합니다."
                ),
                Arguments.of(
                        new AccountCreateRequest(
                                0L,
                                "039",
                                "111111111111"
                        ),
                        "고객 System ID 값은 양의 정수여야합니다."
                ),

                // bankCode 실패케이스
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                null,
                                "111111111111"
                        ),
                        "은행코드는 값이 존재해야합니다."
                ),
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                "",
                                "111111111111"
                        ),
                        "은행코드는 값이 존재해야합니다."
                ),
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                " ",
                                "111111111111"
                        ),
                        "은행코드는 값이 존재해야합니다."
                ),

                // accountNumber 실패케이스
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                "039",
                                null
                        ),
                        "계좌번호는 값이 존재해야합니다."
                ),
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                "039",
                                ""
                        ),
                        "계좌번호는 값이 존재해야합니다."
                ),
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                "039",
                                " "
                        ),
                        "계좌번호는 값이 존재해야합니다."
                ),
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                "039",
                                "1111"
                        ),
                        "계좌번호는 10~20자리여야 합니다."
                ),
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                "039",
                                "1111111111111111111111"
                        ),
                        "계좌번호는 10~20자리여야 합니다."
                )
        );
    }

    @DisplayName("삭제 요청 시, 숫자가 아닌 값이 입력되면 실패한다.")
    @Test
    void deleteAccountWhenNotNumber() throws Exception {
        // given
        String accountId = "abc";

        // when
        ResultActions result = mockMvc.perform(delete("/api/v1/accounts/{accountId}", accountId));

        // then
        result.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"));
    }
}