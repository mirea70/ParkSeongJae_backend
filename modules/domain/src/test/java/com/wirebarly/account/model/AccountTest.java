package com.wirebarly.account.model;

import com.wirebarly.error.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @Test
    @DisplayName("계좌는 생성될 때, 상태: ACTIVE / 잔액: 0 / 삭제일시 null로 초기화된다.")
    void create_initializes_defaults() {
        // given
        AccountStatus initStatus = AccountStatus.ACTIVE;
        long initBalanceVal = 0;
        LocalDateTime initClosedAt = null;

        Long id = 1L;
        Long customerId = 10L;
        String bankCode = BankCode.KOOKMIN.getCode();
        String accountNumber = "009123456789";
        LocalDateTime now = LocalDateTime.now();

        // when
        Account account = Account.createNew(id, customerId, bankCode, accountNumber, now);

        // then
        assertThat(account.getId().getValue()).isEqualTo(id);
        assertThat(account.getCustomerId().getValue()).isEqualTo(customerId);

        BankInfo bankInfo = account.getBankInfo();
        assertThat(bankInfo.getBankCode().getCode()).isEqualTo(bankCode);
        assertThat(bankInfo.getAccountNumber().getValue()).isEqualTo(accountNumber);

        assertThat(account.getStatus()).isEqualTo(initStatus);
        assertThat(account.getBalance().getValue()).isEqualTo(initBalanceVal);
        assertThat(account.getClosedAt()).isEqualTo(initClosedAt);

        assertThat(account.getCreatedAt()).isEqualTo(now);
        assertThat(account.getUpdatedAt()).isEqualTo(now);
        assertThat(account.getUpdatedAt()).isAfterOrEqualTo(account.getCreatedAt());
    }

    @DisplayName("계좌는 해지 시, 해지상태로 변경되며 해지날짜가 기록된다.")
    @Test
    void close() {
        // given
        LocalDateTime now = LocalDateTime.now();

        Account account = Account.createNew(
                1L,
                2L,
                BankCode.IBK.getCode(),
                "123123112132",
                now
                );

        // when
        account.close(now);

        // then
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(account.getClosedAt()).isEqualTo(now);
    }

    @DisplayName("계좌에 입금하면 그 양만큼 현재잔액이 증가하며, 계좌 거래를 생성한다.")
    @Test
    void deposit() {
        // given
        LocalDateTime now = LocalDateTime.now();

        Account account = Account.createNew(
                2L,
                2L,
                "039",
                "123123123123123",
                now
        );

        Long beforeBalance = account.getBalance().getValue();
        Long amount = 10000L;
        LocalDateTime afterAt = LocalDateTime.now().plusDays(1);
        Long accountTransactionId = 1L;

        // when
        AccountTransaction accountTransaction = account.deposit(amount, afterAt, accountTransactionId);

        // then
        Long afterBalance = account.getBalance().getValue();
        assertThat(afterBalance).isEqualTo(beforeBalance + amount);
        assertThat(account.getUpdatedAt()).isEqualTo(afterAt);
        assertThat(accountTransaction).isNotNull();
        assertThat(accountTransaction.getId().getValue()).isEqualTo(accountTransactionId);
    }

    @DisplayName("계좌에 입금할 금액은 양의 정수여야한다.")
    @ParameterizedTest
    @CsvSource(value = {
            "null, 계좌에 입금할 값이 비어있을 수 없습니다.",
            "-1, 계좌에 입금할 값은 양의 정수여야 합니다.",
            "0, 계좌에 입금할 값은 양의 정수여야 합니다.",
    }, nullValues = "null")
    void depositFailByAmount(Long amount, String errorMessage) {
        // given
        LocalDateTime now = LocalDateTime.now();

        Account account = Account.createNew(
                2L,
                2L,
                "039",
                "123123123123123",
                now
        );

        // when // then
        assertThatThrownBy(() -> account.deposit(amount, now.plusDays(1), 1L))
                .isInstanceOf(DomainException.class)
                .hasMessage(errorMessage);
    }

    @DisplayName("해지된 계좌에는 입금할 수 없다.")
    @Test
    void depositFailWhenClosed() {
        // given
        LocalDateTime now = LocalDateTime.now();

        Account account = Account.createNew(
                2L,
                2L,
                "039",
                "123123123123123",
                now
        );
        account.close(now.plusDays(1));

        Long amount = 10000L;

        // when // then
        assertThatThrownBy(() -> account.deposit(amount, now.plusDays(1), 1L))
                .isInstanceOf(DomainException.class)
                .hasMessage("해지된 계좌에는 요청이 불가능합니다.");
    }
}
