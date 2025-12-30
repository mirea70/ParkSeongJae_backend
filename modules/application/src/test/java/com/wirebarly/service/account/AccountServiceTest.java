package com.wirebarly.service.account;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.AccountId;
import com.wirebarly.account.model.BankCode;
import com.wirebarly.customer.model.CustomerId;
import com.wirebarly.error.exception.BusinessException;
import com.wirebarly.in.account.command.AccountCreateCommand;
import com.wirebarly.out.account.AccountOutPort;
import com.wirebarly.out.common.IdGenerator;
import com.wirebarly.out.customer.CustomerOutPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private CustomerOutPort customerOutPort;

    @Mock
    private AccountOutPort accountOutPort;

    @Mock
    private IdGenerator idGenerator;

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
                accountNumber
        );

        given(customerOutPort.isExist(any(CustomerId.class))).willReturn(true);
        given(idGenerator.nextId()).willReturn(accountId);
        given(accountOutPort.insert(any(Account.class)))
                .willReturn(
                        Account.createNew(
                                accountId,
                                customerId,
                                bankCode,
                                accountNumber,
                                now
                        )
                );

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
                accountNumber
        );

        given(customerOutPort.isExist(any(CustomerId.class))).willReturn(false);

        // when // then
        assertThatThrownBy(() -> accountService.register(command))
                .isInstanceOf(BusinessException.class)
                .hasMessage("고객을 찾을 수 없습니다.");
    }

    @DisplayName("계좌가 존재하며, 소유한 고객도 존재하면 비즈니스 로직 수행에 이슈가 없다.")
    @Test
    void remove() {
        // given
        Long accountId = 5L;
        Account account = spy(Account.createNew(
                accountId,
                2L,
                BankCode.IBK.getCode(),
                "1231231231",
                LocalDateTime.now()));

        given(accountOutPort.loadOne(any(AccountId.class))).willReturn(Optional.of(account));
        given(customerOutPort.isExist(any(CustomerId.class))).willReturn(true);

        // when
        accountService.remove(accountId);

        // then
        verify(accountOutPort, times(1)).loadOne(any(AccountId.class));
        verify(account).close(any(LocalDateTime.class));
        verify(customerOutPort, times(1)).isExist(any(CustomerId.class));
        verify(accountOutPort, times(1)).update(account);
    }

    @DisplayName("계좌가 존재하지 않으면 예외를 던진다.")
    @Test
    void removeWhenNoAccount() {
        // given
        given(accountOutPort.loadOne(any(AccountId.class))).willReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> accountService.remove(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("계좌를 찾을 수 없습니다.");
    }

    @DisplayName("계좌를 소유한 고객이 존재하지 않으면 예외를 던진다.")
    @Test
    void removeWhenNoCustomer() {
        // given
        Account account = Account.createNew(
                1L,
                2L,
                BankCode.IBK.getCode(),
                "1231231231",
                LocalDateTime.now()
        );

        given(accountOutPort.loadOne(any(AccountId.class))).willReturn(Optional.of(account));
        given(customerOutPort.isExist(any(CustomerId.class))).willReturn(false);

        // when // then
        assertThatThrownBy(() -> accountService.remove(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("고객을 찾을 수 없습니다.");
    }
}