package com.wirebarly.service.account;

import com.wirebarly.account.model.Account;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

    @DisplayName("신규 계좌를 등록한다. 조회 시 등록한 계좌 정보가 잘 조회된다.")
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
        given(accountOutPort.save(any(Account.class)))
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
        verify(accountOutPort, times(1)).save(any(Account.class));
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
}