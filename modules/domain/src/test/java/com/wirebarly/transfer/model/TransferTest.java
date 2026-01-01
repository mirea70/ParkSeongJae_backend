package com.wirebarly.transfer.model;

import com.wirebarly.common.model.Money;
import com.wirebarly.error.exception.DomainException;
import com.wirebarly.error.info.TransferErrorInfo;
import com.wirebarly.transfer.policy.TransferPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransferTest {

    @Test
    @DisplayName("정상 생성: 한도 이내면 Transfer가 생성되고 amount/fee/time이 기대값으로 세팅된다")
    void createNewSuccessUnderLimit() {
        // given
        Long id = 1L;
        Long fromAccountId = 10L;
        Long toAccountId = 20L;
        Long amount = 100_000L;
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0, 0);

        Long dailyTransferAmount = TransferPolicy.DAILY_TRANSFER_LIMIT - 500_000L;

        // when
        Transfer transfer = Transfer.createNew(id, fromAccountId, toAccountId, amount, now, dailyTransferAmount);

        // then
        assertThat(transfer.getId().getValue()).isEqualTo(id);
        assertThat(transfer.getFromAccountId().getValue()).isEqualTo(fromAccountId);
        assertThat(transfer.getToAccountId().getValue()).isEqualTo(toAccountId);
        assertThat(transfer.getTransferredAt()).isEqualTo(now);

        assertThat(transfer.getAmount().getValue()).isEqualTo(amount);

        long expectedFee = TransferPolicy.calculateFee(new Money(amount)).getValue();
        assertThat(transfer.getFee().getValue()).isEqualTo(expectedFee);
    }

    @Test
    @DisplayName("경계값: 누적 + 금액이 한도와 같으면(=) 초과가 아니므로 생성된다")
    void createNewSuccessEqualToLimit() {
        // given
        Long id = 2L;
        Long fromAccountId = 11L;
        Long toAccountId = 21L;
        Long amount = 100_000L;
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 1, 0, 0);

        Long dailyTransferAmount = TransferPolicy.DAILY_TRANSFER_LIMIT - amount; // 정확히 limit 맞추기

        // when
        Transfer transfer = Transfer.createNew(id, fromAccountId, toAccountId, amount, now, dailyTransferAmount);

        // then
        assertThat(transfer.getAmount().getValue()).isEqualTo(amount);
        assertThat(transfer.getTransferredAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("한도 초과: 누적 + 금액이 한도를 넘으면 DomainException(OVER_TRANSFER_LIMIT)과 overAmount가 포함된다")
    void createNewFailOverLimit() {
        // given
        Long id = 3L;
        Long fromAccountId = 12L;
        Long toAccountId = 22L;
        Long amount = 100_000L;
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 2, 0, 0);

        Long dailyTransferAmount = TransferPolicy.DAILY_TRANSFER_LIMIT - 50_000L; // limit - 50,000 + 100,000 => 50,000 초과
        long expectedOver = 50_000L;

        // when & then
        assertThatThrownBy(() -> Transfer.createNew(id, fromAccountId, toAccountId, amount, now, dailyTransferAmount))
                .isInstanceOf(DomainException.class)
                .satisfies(ex -> {
                    DomainException de = (DomainException) ex;

                    assertThat(de.getErrorInfo()).isEqualTo(TransferErrorInfo.OVER_TRANSFER_LIMIT);
                    assertThat(de.getDetails()).containsAllEntriesOf(
                            Map.of(
                                    "limit", TransferPolicy.DAILY_TRANSFER_LIMIT,
                                    "overAmount", expectedOver
                            )
                    );
                });
    }
}