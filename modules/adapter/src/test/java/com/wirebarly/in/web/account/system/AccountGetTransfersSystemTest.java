package com.wirebarly.in.web.account.system;

import com.wirebarly.in.web.account.response.TransferResponse;
import com.wirebarly.out.persistence.jpa.account.entity.AccountJpaEntity;
import com.wirebarly.out.persistence.jpa.customer.entity.CustomerJpaEntity;
import com.wirebarly.out.persistence.jpa.transfer.entity.TransferJpaEntity;
import com.wirebarly.transfer.model.TransferDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class AccountGetTransfersSystemTest extends AccountSystemTestSupport {

    @DisplayName("지정계좌의 송금/수취내역을 조회하면 오직 지정된 계좌의 내역만 잘 조회된다.")
    @Test
    void getTransfers() {
        // given
        Long accountId1 = 3L;
        Long accountId2 = 4L;
        accountTestDataInit(accountId1, accountId2);

        Long amount1 = 3000L;
        TransferJpaEntity transfer1 = TransferJpaEntity.builder()
                .transferId(1L)
                .fromAccountId(accountId1)
                .toAccountId(accountId2)
                .amount(3000L)
                .fee(30L)
                .transferredAt(LocalDateTime.of(2025, 12, 30, 10, 59))
                .build();

        Long amount2 = 8000L;
        TransferJpaEntity transfer2 = TransferJpaEntity.builder()
                .transferId(2L)
                .fromAccountId(accountId2)
                .toAccountId(accountId1)
                .amount(8000L)
                .fee(80L)
                .transferredAt(LocalDateTime.of(2025, 11, 30, 10, 59))
                .build();

        TransferJpaEntity otherTransfer = TransferJpaEntity.builder()
                .transferId(3L)
                .fromAccountId(11L)
                .toAccountId(333L)
                .amount(10000L)
                .fee(100L)
                .transferredAt(LocalDateTime.of(2024, 11, 30, 10, 59))
                .build();

        transferJpaRepository.save(transfer1);
        transferJpaRepository.save(transfer2);
        transferJpaRepository.save(otherTransfer);

        // when
        ResponseEntity<List<TransferResponse>> response = whenGetTransfers(accountId1);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<TransferResponse> transfers = response.getBody();
        assertThat(transfers).hasSize(2)
                .extracting("counterpartyAccountId", "direction", "amount")
                .containsExactlyInAnyOrder(
                        tuple(accountId2, TransferDirection.OUT.name(), amount1),
                        tuple(accountId2, TransferDirection.IN.name(), amount2)
                );
    }

    @DisplayName("지정계좌의 송금/수취내역을 조회했을 때, 내역이 없으면 빈 리스트를 반환한다.")
    @Test
    void getTransfersWhenEmpty() {
        // given
        Long accountId1 = 3L;
        Long accountId2 = 4L;
        accountTestDataInit(accountId1, accountId2);

        // when
        ResponseEntity<List<TransferResponse>> response = whenGetTransfers(accountId1);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<TransferResponse> transfers = response.getBody();
        assertThat(transfers).hasSize(0);
    }

    private void accountTestDataInit(Long fromAccountId, Long toAccountId) {
        Long customerId = 1L;
        LocalDateTime now = LocalDateTime.now();

        customerJpaRepository.save(
                new CustomerJpaEntity(customerId, "Smith", now, now, null)
        );

        AccountJpaEntity fromAccountJpaEntity = AccountJpaEntity.builder()
                .accountId(fromAccountId)
                .customerId(customerId)
                .bankCode("039")
                .accountNumber("1231231231231")
                .status("ACTIVE")
                .balance(10000L)
                .createdAt(now)
                .updatedAt(now)
                .closedAt(null)
                .build();

        AccountJpaEntity toAccountJpaEntity = AccountJpaEntity.builder()
                .accountId(toAccountId)
                .customerId(customerId)
                .bankCode("039")
                .accountNumber("1231231231235")
                .status("ACTIVE")
                .balance(10000L)
                .createdAt(now)
                .updatedAt(now)
                .closedAt(null)
                .build();

        accountJpaRepository.save(fromAccountJpaEntity);
        accountJpaRepository.save(toAccountJpaEntity);
    }

    private ResponseEntity<List<TransferResponse>> whenGetTransfers(Long accountId) {
        return restClient.get()
                .uri("/api/v1/accounts/{accountId}/transfers", accountId)
                .exchange((request, httpResponse) -> {
                    List<TransferResponse> body = httpResponse.bodyTo(new ParameterizedTypeReference<>() {});
                    return ResponseEntity
                            .status(httpResponse.getStatusCode())
                            .headers(httpResponse.getHeaders())
                            .body(body);
                });
    }
}
