package com.wirebarly.out.persistence.jpa.account.adapter;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.AccountId;
import com.wirebarly.account.model.AccountTransaction;
import com.wirebarly.out.persistence.jpa.PersistenceAdapterJpaTestSupport;
import com.wirebarly.out.persistence.jpa.account.entity.AccountTransactionJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class AccountTransactionPersistenceAdapterTest extends PersistenceAdapterJpaTestSupport {

    @DisplayName("계좌거래 데이터 저장 시, 저장했던 정보가 잘 조회된다.")
    @Test
    void insert() {
        // given
        LocalDateTime now = LocalDateTime.now();

        Account account = Account.createNew(
                1L,
                2L,
                "039",
                "123123123123",
                now
        );
        Long amount = 1000L;
        Long accountTransactionId = 5L;
        AccountTransaction accountTransaction = account.deposit(amount, now, accountTransactionId);

        // when
        accountTransactionPersistenceAdapter.insert(accountTransaction);

        // then
        List<AccountTransactionJpaEntity> results = accountTransactionJpaRepository.findAll();

        assertThat(results).hasSize(1)
                .extracting("accountTransactionId", "amount")
                .containsExactlyInAnyOrder(
                        tuple(accountTransactionId, amount)
                );
    }

    @DisplayName("계좌에서 하루동안 발생한 출금 타입 거래금액의 합계를 반환한다.")
    @Test
    void getDailyWithdrawAmount() {
        // given
        Long accountId = 3L;
        Long amount1 = 1000L;
        LocalDate today = LocalDate.of(2025, 1, 1);

        AccountTransactionJpaEntity transaction1 = AccountTransactionJpaEntity.builder()
                .accountTransactionId(5L)
                .accountId(accountId)
                .transferId(null)
                .type("WITHDRAW")
                .amount(amount1)
                .balanceAfter(5000L)
                .transactedAt(
                        LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 8, 30)
                )
                .build();

        Long amount2 = 3000L;

        AccountTransactionJpaEntity transaction2 = AccountTransactionJpaEntity.builder()
                .accountTransactionId(7L)
                .accountId(accountId)
                .transferId(null)
                .type("WITHDRAW")
                .amount(amount2)
                .balanceAfter(2000L)
                .transactedAt(
                        LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 23, 30)
                )
                .build();

        accountTransactionJpaRepository.save(transaction1);
        accountTransactionJpaRepository.save(transaction2);

        // when
        Long result = accountTransactionPersistenceAdapter.getDailyWithdrawAmount(new AccountId(accountId), today);

        // then
        assertThat(result).isEqualTo(amount1 + amount2);
    }

    @DisplayName("계좌에서 하루동안 발생한 출금 타입 거래금액의 합계를 반환 시, 시작 정각시각도 포함한다.")
    @Test
    void getDailyWithdrawAmountWhenStartSharp() {
        // given
        Long accountId = 3L;
        Long amount1 = 1000L;
        LocalDate today = LocalDate.of(2025, 1, 1);

        AccountTransactionJpaEntity transaction1 = AccountTransactionJpaEntity.builder()
                .accountTransactionId(5L)
                .accountId(accountId)
                .transferId(null)
                .type("WITHDRAW")
                .amount(amount1)
                .balanceAfter(5000L)
                .transactedAt(
                        LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 0, 0)
                )
                .build();

        Long amount2 = 3000L;

        AccountTransactionJpaEntity transaction2 = AccountTransactionJpaEntity.builder()
                .accountTransactionId(7L)
                .accountId(accountId)
                .transferId(null)
                .type("WITHDRAW")
                .amount(amount2)
                .balanceAfter(2000L)
                .transactedAt(
                        LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 23, 30)
                )
                .build();

        accountTransactionJpaRepository.save(transaction1);
        accountTransactionJpaRepository.save(transaction2);

        // when
        Long result = accountTransactionPersistenceAdapter.getDailyWithdrawAmount(new AccountId(accountId), today);

        // then
        assertThat(result).isEqualTo(amount1 + amount2);
    }

    @DisplayName("계좌에서 하루동안 발생한 출금 타입 거래금액의 합계를 반환 시, 다음날 정각시각은 포함되지 않는다.")
    @Test
    void getDailyWithdrawAmountWhenEndSharp() {
        // given
        Long accountId = 3L;
        Long amount1 = 1000L;
        LocalDate today = LocalDate.of(2025, 1, 1);

        AccountTransactionJpaEntity transaction1 = AccountTransactionJpaEntity.builder()
                .accountTransactionId(5L)
                .accountId(accountId)
                .transferId(null)
                .type("WITHDRAW")
                .amount(amount1)
                .balanceAfter(5000L)
                .transactedAt(
                        LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 8, 30)
                )
                .build();

        Long amount2 = 3000L;

        LocalDate tommorrow = today.plusDays(1);

        AccountTransactionJpaEntity transaction2 = AccountTransactionJpaEntity.builder()
                .accountTransactionId(7L)
                .accountId(accountId)
                .transferId(null)
                .type("WITHDRAW")
                .amount(amount2)
                .balanceAfter(2000L)
                .transactedAt(
                        LocalDateTime.of(tommorrow.getYear(), tommorrow.getMonth(), tommorrow.getDayOfMonth(), 0, 0)
                )
                .build();

        accountTransactionJpaRepository.save(transaction1);
        accountTransactionJpaRepository.save(transaction2);

        // when
        Long result = accountTransactionPersistenceAdapter.getDailyWithdrawAmount(new AccountId(accountId), today);

        // then
        assertThat(result).isEqualTo(amount1);
    }
}