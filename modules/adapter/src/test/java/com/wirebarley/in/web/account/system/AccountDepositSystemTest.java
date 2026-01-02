package com.wirebarley.in.web.account.system;

import com.wirebarley.in.web.account.request.AccountDepositRequest;
import com.wirebarley.in.web.error.response.ErrorResponse;
import com.wirebarley.out.persistence.jpa.account.entity.AccountJpaEntity;
import com.wirebarley.out.persistence.jpa.account.entity.AccountTransactionJpaEntity;
import com.wirebarley.out.persistence.jpa.account.repository.AccountJpaRepository;
import com.wirebarley.out.persistence.jpa.account.repository.AccountTransactionJpaRepository;
import com.wirebarley.out.persistence.jpa.customer.entity.CustomerJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class AccountDepositSystemTest extends AccountSystemTestSupport {

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    @Autowired
    private AccountTransactionJpaRepository accountTransactionJpaRepository;

    @DisplayName("계좌에 돈을 입금하면 현재잔액이 그만큼 증가하고, 계좌거래가 생성된다.")
    @Test
    void deposit() {
        // given
        Long customerId = 1L;
        LocalDateTime now = LocalDateTime.now();

        customerJpaRepository.save(
                new CustomerJpaEntity(customerId, "Smith", now, now, null)
        );

        Long accountId = 2L;
        AccountJpaEntity accountJpaEntity = AccountJpaEntity.builder()
                .accountId(accountId)
                .customerId(customerId)
                .bankCode("039")
                .accountNumber("1231231231231")
                .status("ACTIVE")
                .balance(1L)
                .createdAt(now)
                .updatedAt(now)
                .closedAt(null)
                .build();

        accountJpaRepository.save(accountJpaEntity);
        AccountJpaEntity beforeAccount = accountJpaRepository.findById(accountId).orElse(null);
        Objects.requireNonNull(beforeAccount);
        Long balanceBefore = beforeAccount.getBalance();

        Long amount = 1000L;
        AccountDepositRequest request = new AccountDepositRequest(amount);

        // when
        ResponseEntity<Void> response = whenDepositSuccess(accountId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        AccountJpaEntity afterAccount = accountJpaRepository.findById(accountId).orElse(null);
        Objects.requireNonNull(afterAccount);
        Long balanceAfter = afterAccount.getBalance();

        assertThat(balanceAfter).isEqualTo(balanceBefore + amount);

        List<AccountTransactionJpaEntity> accountTransaction = accountTransactionJpaRepository.findAll();
        assertThat(accountTransaction).hasSize(1)
                .extracting("accountId", "balanceAfter")
                .containsExactlyInAnyOrder(
                        tuple(accountId, balanceAfter)
                );
    }

    @DisplayName("입금에 실패하면 현재잔액 변경이 반영되지 않고, 계좌거래가 생성되지 않는다.")
    @Test
    void depositWhenFail() {
        // given
        Long customerId = 1L;
        LocalDateTime now = LocalDateTime.now();

        customerJpaRepository.save(
                new CustomerJpaEntity(customerId, "Smith", now, now, null)
        );

        Long accountId = 2L;
        AccountJpaEntity accountJpaEntity = AccountJpaEntity.builder()
                .accountId(accountId)
                .customerId(customerId)
                .bankCode("039")
                .accountNumber("1231231231231")
                .status("ACTIVE")
                .balance(1L)
                .createdAt(now)
                .updatedAt(now)
                .closedAt(null)
                .build();

        accountJpaRepository.save(accountJpaEntity);
        AccountJpaEntity beforeAccount = accountJpaRepository.findById(accountId).orElse(null);
        Objects.requireNonNull(beforeAccount);
        Long balanceBefore = beforeAccount.getBalance();

        Long amount = -1000L;
        AccountDepositRequest request = new AccountDepositRequest(amount);

        // when
        ResponseEntity<ErrorResponse> response = whenDepositFail(accountId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        AccountJpaEntity afterAccount = accountJpaRepository.findById(accountId).orElse(null);
        Objects.requireNonNull(afterAccount);
        Long balanceAfter = afterAccount.getBalance();

        assertThat(balanceAfter).isEqualTo(balanceBefore);

        List<AccountTransactionJpaEntity> accountTransaction = accountTransactionJpaRepository.findAll();
        assertThat(accountTransaction).hasSize(0);
    }

    private ResponseEntity<Void> whenDepositSuccess(Long accountId, AccountDepositRequest request) {
        return restClient.post()
                .uri("/api/v1/accounts/{accountId}/deposit", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    private ResponseEntity<ErrorResponse> whenDepositFail(Long accountId, AccountDepositRequest request) {
        return restClient.post()
                .uri("/api/v1/accounts/{accountId}/deposit", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange((req, res) -> {
                    ErrorResponse body = res.bodyTo(ErrorResponse.class);
                    return new ResponseEntity<>(body, res.getHeaders(), res.getStatusCode());
                });
    }
}
