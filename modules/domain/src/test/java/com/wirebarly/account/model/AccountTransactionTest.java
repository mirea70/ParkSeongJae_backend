package com.wirebarly.account.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTransactionTest {

    @Test
    @DisplayName("이체 거래 생성 시 모든 값이 도메인 객체로 변환된다.")
    void createNew_withTransfer() {
        // given
        Long accountTransactionId = 1L;
        Long accountId = 100L;
        Long transferId = 200L;

        // when
        AccountTransaction accountTransaction = AccountTransaction.createNew(
                accountTransactionId,
                accountId,
                transferId,
                "WITHDRAW",
                1000L,
                9000L,
                LocalDateTime.now()
        );

        // then
        assertThat(accountTransaction.getId().getValue()).isEqualTo(accountTransactionId);
        assertThat(accountTransaction.getAccountId().getValue()).isEqualTo(accountId);
        assertThat(accountTransaction.getTransferId()).isNotNull();
        assertThat(accountTransaction.getType()).isEqualTo(AccountTransactionType.WITHDRAW);
    }

    @Test
    @DisplayName("이체가 아닌 거래는 이체 시스템 ID가 null이다.")
    void createNew_withoutTransfer() {
        AccountTransaction accountTransaction = AccountTransaction.createNew(
                1L,
                100L,
                null,
                "DEPOSIT",
                1000L,
                5000L,
                LocalDateTime.now()
        );

        assertThat(accountTransaction.getTransferId()).isNull();
    }
}