package com.wirebarly.account.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {

    @Test
    @DisplayName("생성 시, 기본 상태: ACTIVE / 잔액: 0 / 삭제일시 null로 초기화한다")
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
}
