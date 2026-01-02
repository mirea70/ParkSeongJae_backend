package com.wirebarly.service.account;

import com.wirebarly.account.model.Account;
import com.wirebarly.customer.model.CustomerId;
import com.wirebarly.error.exception.BusinessException;
import com.wirebarly.error.info.CustomerErrorInfo;
import com.wirebarly.in.account.command.AccountCreateCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AccountServiceRegisterTest extends AccountServiceTestSupport {

        @DisplayName("신규 계좌 등록 시, 요청 고객이 존재하고 채번이 잘 되었으면 비즈니스 로직 수행에 문제가 없다.")
        @Test
        void register() {
                // given
                Long accountId = 5L;
                Long customerId = 2L;
                String bankCode = "039";
                String accountNumber = "13333231231231";
                LocalDateTime now = LocalDateTime.now();

                AccountCreateCommand command = new AccountCreateCommand(
                                customerId,
                                bankCode,
                                accountNumber);

                given(customerOutPort.isExist(any(CustomerId.class))).willReturn(true);
                given(idGenerator.nextId()).willReturn(accountId);
                given(accountOutPort.insert(any(Account.class)))
                                .willReturn(
                                                Account.createNew(
                                                                accountId,
                                                                customerId,
                                                                bankCode,
                                                                accountNumber,
                                                                now));

                // when
                accountService.register(command);
                // then
                verify(customerOutPort, times(1)).isExist(any(CustomerId.class));
                verify(accountOutPort, times(1)).insert(any(Account.class));
                verify(idGenerator, times(1)).nextId();
        }

        @DisplayName("계좌 등록을 요청한 고객의 정보가 존재하지 않을경우 예외를 던진다.")
        @Test
        void registerWhenIsNotExistCustomer() {
                // given
                Long customerId = 2L;
                String bankCode = "039";
                String accountNumber = "13333231231231";

                AccountCreateCommand command = new AccountCreateCommand(
                                customerId,
                                bankCode,
                                accountNumber);

                given(customerOutPort.isExist(any(CustomerId.class))).willReturn(false);

                // when // then
                assertThatThrownBy(() -> accountService.register(command))
                                .isInstanceOf(BusinessException.class)
                                .extracting("errorInfo")
                                .isEqualTo(CustomerErrorInfo.NOT_FOUND);
        }
}