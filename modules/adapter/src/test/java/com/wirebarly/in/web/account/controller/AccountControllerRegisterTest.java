package com.wirebarly.in.web.account.controller;

import com.wirebarly.in.account.usecase.AccountUseCase;
import com.wirebarly.in.transfer.usecase.TransferUseCase;
import com.wirebarly.in.web.ControllerTestSupport;
import com.wirebarly.in.web.account.request.AccountCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountControllerRegisterTest extends ControllerTestSupport {

    @MockitoBean
    private AccountUseCase accountUseCase;

    @MockitoBean
    private TransferUseCase transferUseCase;

    @DisplayName("계좌 등록 성공케이스")
    @Test
    void registerAccount() throws Exception {
        // given
        AccountCreateRequest request = new AccountCreateRequest(1L, "039", "1234567890");
        long accountId = 10L;
        String status = "ACTIVE";

        given(accountUseCase.register(request.toCommand())).willReturn(
                new com.wirebarly.in.account.result.AccountResult(
                        accountId,
                        request.customerId(),
                        request.bankCode(),
                        request.accountNumber(),
                        status,
                        0L,
                        java.time.LocalDateTime.now(),
                        java.time.LocalDateTime.now(),
                        null));

        // when
        ResultActions result = postRequest("/api/v1/accounts/new", request);

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId))
                .andExpect(jsonPath("$.customerId").value(request.customerId()))
                .andExpect(jsonPath("$.bankCode").value(request.bankCode()))
                .andExpect(jsonPath("$.accountNumber").value(request.accountNumber()))
                .andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.balance").value(0L))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(jsonPath("$.closedAt").isEmpty());
    }

    @DisplayName("계좌 등록 실패케이스")
    @ParameterizedTest
    @MethodSource("accountCreateRequestCases")
    void registerAccountWhenFail(AccountCreateRequest request, String errorMessage) throws Exception {
        // when // then
        postRequest("/api/v1/accounts/new", request)
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
                                "111111111111"),
                        "고객 System ID 값은 존재해야합니다."),
                Arguments.of(
                        new AccountCreateRequest(
                                -1L,
                                "039",
                                "111111111111"),
                        "고객 System ID 값은 양의 정수여야합니다."),
                Arguments.of(
                        new AccountCreateRequest(
                                0L,
                                "039",
                                "111111111111"),
                        "고객 System ID 값은 양의 정수여야합니다."),

                // bankCode 실패케이스
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                null,
                                "111111111111"),
                        "은행코드는 값이 존재해야합니다."),
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                "",
                                "111111111111"),
                        "은행코드는 값이 존재해야합니다."),
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                " ",
                                "111111111111"),
                        "은행코드는 값이 존재해야합니다."),

                // accountNumber 실패케이스
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                "039",
                                null),
                        "계좌번호는 값이 존재해야합니다."),
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                "039",
                                ""),
                        "계좌번호는 값이 존재해야합니다."),
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                "039",
                                " "),
                        "계좌번호는 값이 존재해야합니다."),
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                "039",
                                "1111"),
                        "계좌번호는 10~20자리여야 합니다."),
                Arguments.of(
                        new AccountCreateRequest(
                                1L,
                                "039",
                                "1111111111111111111111"),
                        "계좌번호는 10~20자리여야 합니다."));
    }
}
