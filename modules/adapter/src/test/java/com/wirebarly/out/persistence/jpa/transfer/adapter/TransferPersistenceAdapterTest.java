package com.wirebarly.out.persistence.jpa.transfer.adapter;

import com.wirebarly.account.model.AccountId;
import com.wirebarly.in.transfer.result.TransferResult;
import com.wirebarly.out.persistence.jpa.PersistenceAdapterJpaTestSupport;
import com.wirebarly.out.persistence.jpa.transfer.entity.TransferJpaEntity;
import com.wirebarly.transfer.model.Transfer;
import com.wirebarly.transfer.model.TransferDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class TransferPersistenceAdapterTest extends PersistenceAdapterJpaTestSupport {

    @DisplayName("송금 데이터 저장 시, 저장했던 정보가 잘 조회된다.")
    @Test
    void insert() {
        // given
        LocalDateTime now = LocalDateTime.now();
        Long transferId = 2L;
        Long amount = 1000L;

        Transfer transfer = Transfer.createNew(
                transferId,
                1L,
                3L,
                amount,
                now,
                50000L
        );

        // when
        transferPersistenceAdapter.insert(transfer);

        // then
        List<TransferJpaEntity> results = transferJpaRepository.findAll();

        assertThat(results).hasSize(1)
                .extracting("transferId", "amount")
                .containsExactly(
                        tuple(transferId, amount)
                );
    }

    @DisplayName("송금자 기준 하루동안 송금액의 합계를 반환한다.")
    @Test
    void getDailyTransferAmount() {
        // given
        Long fromAccountId = 3L;
        Long amount1 = 1000L;
        LocalDate today = LocalDate.of(2025, 1, 1);

        TransferJpaEntity transfer1 = TransferJpaEntity.builder()
                .transferId(1L)
                .fromAccountId(fromAccountId)
                .toAccountId(3L)
                .amount(amount1)
                .fee(10L)
                .transferredAt(LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 8, 30))
                .build();

        Long amount2 = 3000L;

        TransferJpaEntity transfer2 = TransferJpaEntity.builder()
                .transferId(2L)
                .fromAccountId(fromAccountId)
                .toAccountId(5L)
                .amount(amount2)
                .fee(30L)
                .transferredAt(LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 23, 30))
                .build();

        transferJpaRepository.save(transfer1);
        transferJpaRepository.save(transfer2);

        // when
        Long result = transferPersistenceAdapter.getDailyTransferAmount(new AccountId(fromAccountId), today);

        // then
        assertThat(result).isEqualTo(amount1 + amount2);
    }

    @DisplayName("송금액 합계를 반환 시, 시작 정각시각도 포함한다.")
    @Test
    void getDailyTransferAmountWhenStartSharp() {
        // given
        Long fromAccountId = 3L;
        Long amount1 = 1000L;
        LocalDate today = LocalDate.of(2025, 1, 1);

        TransferJpaEntity transfer1 = TransferJpaEntity.builder()
                .transferId(1L)
                .fromAccountId(fromAccountId)
                .toAccountId(3L)
                .amount(amount1)
                .fee(10L)
                .transferredAt(
                        LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 0, 0)
                )
                .build();

        Long amount2 = 3000L;

        TransferJpaEntity transfer2 = TransferJpaEntity.builder()
                .transferId(2L)
                .fromAccountId(fromAccountId)
                .toAccountId(5L)
                .amount(amount2)
                .fee(30L)
                .transferredAt(LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 23, 30))
                .build();

        transferJpaRepository.save(transfer1);
        transferJpaRepository.save(transfer2);

        // when
        Long result = transferPersistenceAdapter.getDailyTransferAmount(new AccountId(fromAccountId), today);

        // then
        assertThat(result).isEqualTo(amount1 + amount2);
    }

    @DisplayName("송금액의 합계 반환 시, 다음날 정각시각은 포함되지 않는다.")
    @Test
    void getDailyTransferAmountWhenEndSharp() {
        // given
        Long fromAccountId = 3L;
        Long amount1 = 1000L;
        LocalDate today = LocalDate.of(2025, 1, 1);

        TransferJpaEntity transfer1 = TransferJpaEntity.builder()
                .transferId(1L)
                .fromAccountId(fromAccountId)
                .toAccountId(3L)
                .amount(amount1)
                .fee(10L)
                .transferredAt(
                        LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 8, 30)
                )
                .build();

        Long amount2 = 3000L;
        LocalDate tomorrow = today.plusDays(1);

        TransferJpaEntity transfer2 = TransferJpaEntity.builder()
                .transferId(2L)
                .fromAccountId(fromAccountId)
                .toAccountId(5L)
                .amount(amount2)
                .fee(30L)
                .transferredAt(
                        LocalDateTime.of(tomorrow.getYear(), tomorrow.getMonth(), tomorrow.getDayOfMonth(), 0, 0)
                )
                .build();

        transferJpaRepository.save(transfer1);
        transferJpaRepository.save(transfer2);

        // when
        Long result = transferPersistenceAdapter.getDailyTransferAmount(new AccountId(fromAccountId), today);

        // then
        assertThat(result).isEqualTo(amount1);
    }

    @Test
    @DisplayName("지정 계좌의 송금/수취 내역을 최신순으로 조회하고 direction/counterparty/fee 매핑이 맞다")
    void getTransfersBy() {
        // given
        long target = 10L;

        // target 계좌가 OUT (from=10)
        LocalDateTime time1 = LocalDateTime.of(2026, 1, 1, 10, 0, 0);
        saveTransfer(1L, 10L, 20L, 100_000L, 1_000L, time1);

        LocalDateTime time2 = LocalDateTime.of(2026, 1, 1, 12, 0, 0);
        // target 계좌가 IN (to=10)
        saveTransfer(2L, 30L, 10L, 50_000L, 500L, time2);

        // target과 무관한 transfer
        saveTransfer(3L, 99L, 98L, 1_000L, 10L, time2);

        // when
        List<TransferResult> result = transferPersistenceAdapter.getTransfersBy(new AccountId(target));

        // then
        // 최신순 정렬: 11:00(IN) -> 10:00(OUT)
        assertThat(result).hasSize(2);

        TransferResult first = result.get(0);
        assertThat(first.transferId()).isEqualTo(2L);
        assertThat(first.direction()).isEqualTo(TransferDirection.IN.name());
        assertThat(first.counterpartyAccountId()).isEqualTo(30L);  // IN이면 from이 상대
        assertThat(first.amount()).isEqualTo(50_000L);
        assertThat(first.fee()).isEqualTo(0L);
        assertThat(first.transferredAt()).isEqualTo(time2);

        TransferResult second = result.get(1);
        assertThat(second.transferId()).isEqualTo(1L);
        assertThat(second.direction()).isEqualTo(TransferDirection.OUT.name());
        assertThat(second.counterpartyAccountId()).isEqualTo(20L);  // OUT이면 to가 상대
        assertThat(second.amount()).isEqualTo(100_000L);
        assertThat(second.fee()).isEqualTo(1_000L);
        assertThat(second.transferredAt()).isEqualTo(time1);
    }

    private void saveTransfer(
            Long transferId,
            Long fromAccountId,
            Long toAccountId,
            Long amount,
            Long fee,
            LocalDateTime transferredAt
    ) {
        TransferJpaEntity transfer = TransferJpaEntity.builder()
                .transferId(transferId)
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .amount(amount)
                .fee(fee)
                .transferredAt(transferredAt)
                .build();

        transferJpaRepository.save(transfer);
    }
}