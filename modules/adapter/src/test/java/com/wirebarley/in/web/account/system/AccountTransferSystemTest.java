package com.wirebarley.in.web.account.system;

import com.wirebarley.account.model.AccountTransactionType;
import com.wirebarley.in.web.account.request.TransferCreateRequest;
import com.wirebarley.in.web.error.response.ErrorResponse;
import com.wirebarley.out.persistence.jpa.account.entity.AccountJpaEntity;
import com.wirebarley.out.persistence.jpa.account.entity.AccountTransactionJpaEntity;
import com.wirebarley.out.persistence.jpa.customer.entity.CustomerJpaEntity;
import com.wirebarley.out.persistence.jpa.transfer.entity.TransferJpaEntity;
import com.wirebarley.transfer.policy.TransferPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class AccountTransferSystemTest extends AccountSystemTestSupport {

    @DisplayName("송금하면 송금 데이터가 생성되고, 송금액에 따라 송신계좌의 출금거래(송금액/수수료), 수신계좌의 입금거래가 생성된다.")
    @Test
    void transfer() {
        // given
        Long fromAccountId = 2L;
        Long toAccountId = 5L;
        accountTestDataInit(fromAccountId, toAccountId);

        // 잔액비교 검증 위해, 이전 계좌 잔액 조회
        AccountJpaEntity beforeFromAccount = accountJpaRepository.findById(fromAccountId).orElse(null);
        Objects.requireNonNull(beforeFromAccount);
        Long beforeFromBalance = beforeFromAccount.getBalance();

        AccountJpaEntity beforeToAccount = accountJpaRepository.findById(toAccountId).orElse(null);
        Objects.requireNonNull(beforeToAccount);
        Long beforeToBalance = beforeToAccount.getBalance();


        Long transferAmount = 1000L;
        TransferCreateRequest request = new TransferCreateRequest(toAccountId, transferAmount);


        // when
        ResponseEntity<Void> response = whenTransferSuccess(fromAccountId, request);


        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // - 송금 거래 생성 검증
        List<TransferJpaEntity> savedTransfers = transferJpaRepository.findAll();
        assertThat(savedTransfers).hasSize(1)
                .extracting("fromAccountId", "toAccountId", "amount")
                .containsExactly(
                        tuple(fromAccountId, toAccountId, transferAmount)
                );

        Long transferId = savedTransfers.get(0).getTransferId();

        // - 송신자 출금 거래 생성 검증
        List<AccountTransactionJpaEntity> fromAccountTransaction = accountTransactionJpaRepository.findAllByAccountId(fromAccountId);
        assertThat(fromAccountTransaction).hasSize(2)
                .extracting("accountId", "transferId", "type")
                .containsExactlyInAnyOrder(
                        tuple(fromAccountId, transferId, AccountTransactionType.WITHDRAW.name()),
                        tuple(fromAccountId, transferId, AccountTransactionType.WITHDRAW.name())
                );

        // - 수신자 입금 거래 생성 검증
        List<AccountTransactionJpaEntity> toAccountTransaction = accountTransactionJpaRepository.findAllByAccountId(toAccountId);
        assertThat(toAccountTransaction).hasSize(1)
                .extracting("accountId", "transferId", "type")
                .containsExactlyInAnyOrder(
                        tuple(toAccountId, transferId,  AccountTransactionType.DEPOSIT.name())
                );

        // - 송신자 잔액전후 비교
        AccountJpaEntity afterFromAccount = accountJpaRepository.findById(fromAccountId).orElse(null);
        Objects.requireNonNull(afterFromAccount);
        Long afterFromBalance = afterFromAccount.getBalance();

        assertThat(afterFromBalance).isLessThan(beforeFromBalance);

        // - 수신자 잔액전후 비교
        AccountJpaEntity afterToAccount = accountJpaRepository.findById(toAccountId).orElse(null);
        Objects.requireNonNull(afterToAccount);
        Long afterToBalance = afterToAccount.getBalance();

        assertThat(afterToBalance).isEqualTo(beforeToBalance + transferAmount);
    }

    @DisplayName("송금에 실패하면 송금/송신계좌의 출금거래/수신계좌의 입금거래가 생성되지 않고, 각 계좌의 잔액도 변화가 없다.")
    @Test
    void transferWhenFail() {
        // given
        Long fromAccountId = 2L;
        Long toAccountId = 5L;
        accountTestDataInit(fromAccountId, toAccountId);

        // 잔액비교 검증 위해, 이전 계좌 잔액 조회
        AccountJpaEntity beforeFromAccount = accountJpaRepository.findById(fromAccountId).orElse(null);
        Objects.requireNonNull(beforeFromAccount);
        Long beforeFromBalance = beforeFromAccount.getBalance();

        AccountJpaEntity beforeToAccount = accountJpaRepository.findById(toAccountId).orElse(null);
        Objects.requireNonNull(beforeToAccount);
        Long beforeToBalance = beforeToAccount.getBalance();


        Long transferAmount = TransferPolicy.DAILY_TRANSFER_LIMIT + 10000L; // 한도 초과 Trigger
        TransferCreateRequest request = new TransferCreateRequest(toAccountId, transferAmount);


        // when
        ResponseEntity<ErrorResponse> response = whenTransferFail(fromAccountId, request);


        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        // - 송금 거래 생성 검증
        List<TransferJpaEntity> savedTransfers = transferJpaRepository.findAll();
        assertThat(savedTransfers).hasSize(0);

        // - 송신자 출금 거래 생성 검증
        List<AccountTransactionJpaEntity> fromAccountTransaction = accountTransactionJpaRepository.findAllByAccountId(fromAccountId);
        assertThat(fromAccountTransaction).hasSize(0);

        // - 수신자 입금 거래 생성 검증
        List<AccountTransactionJpaEntity> toAccountTransaction = accountTransactionJpaRepository.findAllByAccountId(toAccountId);
        assertThat(toAccountTransaction).hasSize(0);

        // - 송신자 잔액전후 비교
        AccountJpaEntity afterFromAccount = accountJpaRepository.findById(fromAccountId).orElse(null);
        Objects.requireNonNull(afterFromAccount);
        Long afterFromBalance = afterFromAccount.getBalance();

        assertThat(afterFromBalance).isEqualTo(beforeFromBalance);

        // - 수신자 잔액전후 비교
        AccountJpaEntity afterToAccount = accountJpaRepository.findById(toAccountId).orElse(null);
        Objects.requireNonNull(afterToAccount);
        Long afterToBalance = afterToAccount.getBalance();

        assertThat(afterToBalance).isEqualTo(beforeToBalance);
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

    private ResponseEntity<Void> whenTransferSuccess(Long accountId, TransferCreateRequest request) {
        return restClient.post()
                .uri("/api/v1/accounts/{accountId}/transfer", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    private ResponseEntity<ErrorResponse> whenTransferFail(Long accountId, TransferCreateRequest request) {
        return restClient.post()
                .uri("/api/v1/accounts/{accountId}/transfer", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange((req, res) -> {
                    ErrorResponse body = res.bodyTo(ErrorResponse.class);
                    return new ResponseEntity<>(body, res.getHeaders(), res.getStatusCode());
                });
    }
}
