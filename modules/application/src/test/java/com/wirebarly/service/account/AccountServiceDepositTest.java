package com.wirebarly.service.account;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.AccountId;
import com.wirebarly.account.model.AccountTransaction;
import com.wirebarly.account.model.BankCode;
import com.wirebarly.customer.model.CustomerId;
import com.wirebarly.error.exception.BusinessException;
import com.wirebarly.error.info.AccountErrorInfo;
import com.wirebarly.error.info.CustomerErrorInfo;
import com.wirebarly.in.account.command.AccountDepositCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class AccountServiceDepositTest extends AccountServiceTestSupport {

    @DisplayName("계좌에 입금하면 입금 거래를 생성하고 현재잔액을 그만큼 증가시킨다.")
    @Test
    void deposit() {
        // given
        Long accountId = 5L;
        Account account = spy(Account.createNew(
                accountId,
                2L,
                BankCode.IBK.getCode(),
                "1231231231",
                LocalDateTime.now()));
        Long accountTransactionId = 1L;

        given(accountOutPort.loadOneForUpdate(any(AccountId.class))).willReturn(Optional.of(account));
        given(customerOutPort.isExist(any(CustomerId.class))).willReturn(true);
        given(idGenerator.nextId()).willReturn(accountTransactionId);

        // when
        accountService.deposit(accountId, new AccountDepositCommand(1000L));

        // then
        verify(accountOutPort, times(1)).loadOneForUpdate(any(AccountId.class));
        verify(account).deposit(any(Long.class), any(LocalDateTime.class), any(Long.class));
        verify(customerOutPort, times(1)).isExist(any(CustomerId.class));
        verify(accountOutPort, times(1)).update(account);
        verify(accountTransactionOutPort, times(1)).insert(any(AccountTransaction.class));
    }

    @DisplayName("계좌가 존재하지 않으면 예외를 던진다.")
    @Test
    void depositFailWhenNoAccount() {
        // given
        given(accountOutPort.loadOneForUpdate(any(AccountId.class))).willReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> accountService.deposit(1L, new AccountDepositCommand(1000L)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorInfo")
                .isEqualTo(AccountErrorInfo.NOT_FOUND);
    }

    @DisplayName("계좌를 소유한 고객이 존재하지 않으면 예외를 던진다.")
    @Test
    void depositFailWhenNoCustomer() {
        // given
        Account account = Account.createNew(
                1L,
                2L,
                BankCode.IBK.getCode(),
                "1231231231",
                LocalDateTime.now()
        );

        given(accountOutPort.loadOneForUpdate(any(AccountId.class))).willReturn(Optional.of(account));
        given(customerOutPort.isExist(any(CustomerId.class))).willReturn(false);

        // when // then
        assertThatThrownBy(() -> accountService.deposit(1L, new AccountDepositCommand(1000L)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorInfo")
                .isEqualTo(CustomerErrorInfo.NOT_FOUND);
    }
}