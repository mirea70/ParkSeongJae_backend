package com.wirebarly.service.transfer;

import com.wirebarly.ServiceTestSupport;
import com.wirebarly.TestLoaded;
import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.AccountId;
import com.wirebarly.account.model.AccountTransaction;
import com.wirebarly.common.model.Loaded;
import com.wirebarly.error.exception.DomainException;
import com.wirebarly.in.transfer.command.TransferCreateCommand;
import com.wirebarly.in.transfer.result.TransferResult;
import com.wirebarly.out.account.AccountOutPort;
import com.wirebarly.out.account.AccountTransactionOutPort;
import com.wirebarly.out.transfer.TransferOutPort;
import com.wirebarly.service.account.AccountService;
import com.wirebarly.transfer.model.Transfer;
import com.wirebarly.transfer.policy.TransferPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TransferServiceTest extends ServiceTestSupport {
    @InjectMocks
    private TransferService transferService;

    @Mock
    private AccountService accountService;
    @Mock
    private AccountOutPort accountOutPort;
    @Mock
    private TransferOutPort transferOutPort;
    @Mock
    private AccountTransactionOutPort accountTransactionOutPort;

    @Captor
    private ArgumentCaptor<List<AccountTransaction>> accountTransactionListCaptor;

    @Test
    @DisplayName("송금 성공: 송금 생성 후 출금(금액/수수료) 및 입금 거래를 만들고 transfer/transactions를 저장한다")
    void transferAmountSuccess() {
        // given
        Long fromAccountId = 10L;
        Long toAccountId = 20L;
        Long amount = 100_000L;

        TransferCreateCommand command = new TransferCreateCommand(fromAccountId, toAccountId, amount);
        LocalDateTime now = LocalDateTime.now();

        Account fromAccount = spy(Account.fromOutside(
                fromAccountId,
                2L,
                "039",
                "123123123123123",
                "ACTIVE",
                200000L,
                now,
                now,
                null
        ));
        Account toAccount = spy(Account.fromOutside(
                toAccountId,
                2L,
                "039",
                "123123123123123",
                "ACTIVE",
                20000L,
                now,
                now,
                null
        ));

        Loaded<Account> loadedFromAccount = new TestLoaded<>(fromAccount);
        Loaded<Account> loadedToAccount   = new TestLoaded<>(toAccount);

        given(accountService.getValidatedAccountForUpdate(fromAccountId)).willReturn(loadedFromAccount);
        given(accountService.getValidatedAccountForUpdate(toAccountId)).willReturn(loadedToAccount);

        Long transferId = 1L;
        Long accountTransactionId1 = 101L;
        Long accountTransactionId2 = 102L;
        Long accountTransactionId3 = 103L;
        given(idGenerator.nextId()).willReturn(transferId, accountTransactionId1, accountTransactionId2, accountTransactionId3);

        // 일일 이체/출금 누적금액
        given(transferOutPort.getDailyTransferAmount(any(AccountId.class), any(LocalDate.class))).willReturn(0L);
        given(accountTransactionOutPort.getDailyWithdrawAmount(any(AccountId.class), any(LocalDate.class))).willReturn(0L);

        // fromAccount.withdraw / toAccount.deposit 가 반환할 거래 객체들
        AccountTransaction accountTransactionWithdrawAmount = mock(AccountTransaction.class);
        AccountTransaction accountTransactionWithdrawFee = mock(AccountTransaction.class);
        AccountTransaction accountTransactionDepositAmount = mock(AccountTransaction.class);

        // withdraw 2번, deposit 1번이므로 순서대로 리턴
        doReturn(accountTransactionWithdrawAmount, accountTransactionWithdrawFee).when(fromAccount)
                .withdraw(anyLong(), any(LocalDateTime.class), anyLong(), anyLong(), anyLong(), anyString());
        doReturn(accountTransactionDepositAmount).when(toAccount)
                .deposit(anyLong(), any(LocalDateTime.class), anyLong(), anyLong(), anyString());

        // when
        transferService.transfer(command);

        // then

        // - 계좌 조회
        verify(accountService).getValidatedAccountForUpdate(fromAccountId);
        verify(accountService).getValidatedAccountForUpdate(toAccountId);

        // - 누적금액 조회
        verify(transferOutPort).getDailyTransferAmount(eq(fromAccount.getId()), any(LocalDate.class));
        verify(accountTransactionOutPort).getDailyWithdrawAmount(eq(fromAccount.getId()), any(LocalDate.class));

        // - 도메인 메서드 실행
        verify(fromAccount).withdraw(anyLong(), any(LocalDateTime.class), eq(accountTransactionId1), anyLong(), anyLong(), anyString());
        verify(fromAccount).withdraw(anyLong(), any(LocalDateTime.class), eq(accountTransactionId2), anyLong(), anyLong(), anyString());
        verify(toAccount).deposit(anyLong(), any(LocalDateTime.class), eq(accountTransactionId3), anyLong(), anyString());

        // - 저장 호출 검증
        verify(transferOutPort, times(1)).insert(any(Transfer.class));
        verify(accountTransactionOutPort, times(1)).insert(accountTransactionListCaptor.capture());
        verify(accountOutPort, times(1)).applyBalance(loadedFromAccount);
        verify(accountOutPort, times(1)).applyBalance(loadedToAccount);

        verifyNoMoreInteractions(transferOutPort, accountTransactionOutPort, accountOutPort, accountService, idGenerator);
    }

    @Test
    @DisplayName("송금 실패: 한도 초과 등으로 예외가 발생하면 DB 반영 함수가 호출되지 않는다")
    void transferAmountFailWhenTransferCreateThrows() {
        // given
        Long fromAccountId = 10L;
        Long toAccountId = 20L;
        LocalDateTime now = LocalDateTime.now();
        TransferCreateCommand command = new TransferCreateCommand(
                fromAccountId,
                toAccountId,
                TransferPolicy.DAILY_TRANSFER_LIMIT + 1000L);

        Account fromAccount = spy(Account.fromOutside(
                fromAccountId,
                2L,
                "039",
                "123123123123123",
                "ACTIVE",
                200000L,
                now,
                now,
                null
        ));
        Account toAccount = spy(Account.fromOutside(
                toAccountId,
                2L,
                "039",
                "123123123123123",
                "ACTIVE",
                20000L,
                now,
                now,
                null
        ));

        Loaded<Account> loadedFromAccount = new TestLoaded<>(fromAccount);
        Loaded<Account> loadedToAccount   = new TestLoaded<>(toAccount);

        given(accountService.getValidatedAccountForUpdate(fromAccountId)).willReturn(loadedFromAccount);
        given(accountService.getValidatedAccountForUpdate(toAccountId)).willReturn(loadedToAccount);

        // 오늘 누적 이체액이 이미 한도 달성
        given(transferOutPort.getDailyTransferAmount(any(AccountId.class), any(LocalDate.class))).willReturn(TransferPolicy.DAILY_TRANSFER_LIMIT);
        given(idGenerator.nextId()).willReturn(1L);

        // when // then
        assertThatThrownBy(() -> transferService.transfer(command))
                .isInstanceOf(DomainException.class);

        verify(transferOutPort, never()).insert(any());
        verify(accountTransactionOutPort, never()).insert(anyList());

        verify(fromAccount, never()).withdraw(anyLong(), any(), anyLong(), anyLong());
        verify(toAccount, never()).deposit(anyLong(), any(), anyLong());
    }

    @DisplayName("지정된 계좌의 송금/수취 내역을 조회한다")
    @Test
    void getTransfers() {
        // given
        Long accountIdValue = 10L;

        Account account = mock(Account.class);
        AccountId accountId = new AccountId(accountIdValue);

        Loaded<Account> loadedAccount = new TestLoaded<>(account);

        given(accountService.getValidatedAccount(accountIdValue)).willReturn(loadedAccount);
        given(account.getId()).willReturn(accountId);

        List<TransferResult> responses = List.of(
                mock(TransferResult.class),
                mock(TransferResult.class)
        );

        given(transferOutPort.getTransfersBy(accountId)).willReturn(responses);

        // when
        List<TransferResult> result = transferService.getTransfers(accountIdValue);

        // then
        assertThat(result).isSameAs(responses);

        verify(accountService).getValidatedAccount(accountIdValue);
        verify(transferOutPort).getTransfersBy(accountId);
        verifyNoMoreInteractions(accountService, transferOutPort);
    }
}