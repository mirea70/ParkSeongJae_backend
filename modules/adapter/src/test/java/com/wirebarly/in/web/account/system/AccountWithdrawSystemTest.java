package com.wirebarly.in.web.account.system;

import com.wirebarly.in.web.account.request.AccountWithdrawRequest;
import com.wirebarly.in.web.error.response.ErrorResponse;
import com.wirebarly.out.persistence.jpa.account.entity.AccountJpaEntity;
import com.wirebarly.out.persistence.jpa.account.entity.AccountTransactionJpaEntity;
import com.wirebarly.out.persistence.jpa.customer.entity.CustomerJpaEntity;
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

public class AccountWithdrawSystemTest extends AccountSystemTestSupport {

    @DisplayName("계좌에서 돈을 출금하면 현재잔액이 그만큼 감소하고, 계좌거래가 생성된다.")
    @Test
    void withdraw() {
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
                .balance(10000L)
                .createdAt(now)
                .updatedAt(now)
                .closedAt(null)
                .build();

        accountJpaRepository.save(accountJpaEntity);
        AccountJpaEntity beforeAccount = accountJpaRepository.findById(accountId).orElse(null);
        Objects.requireNonNull(beforeAccount);
        Long balanceBefore = beforeAccount.getBalance();

        Long amount = 1000L;
        AccountWithdrawRequest request = new AccountWithdrawRequest(amount);

        // when
        ResponseEntity<Void> response = whenWithdrawSuccess(accountId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        AccountJpaEntity afterAccount = accountJpaRepository.findById(accountId).orElse(null);
        Objects.requireNonNull(afterAccount);
        Long balanceAfter = afterAccount.getBalance();

        assertThat(balanceAfter).isEqualTo(balanceBefore - amount);

        List<AccountTransactionJpaEntity> accountTransaction = accountTransactionJpaRepository.findAll();
        assertThat(accountTransaction).hasSize(1)
                .extracting("accountId", "balanceAfter")
                .containsExactlyInAnyOrder(
                        tuple(accountId, balanceAfter)
                );
    }

    @DisplayName("출금에 실패하면 현재잔액 변경이 반영되지 않고, 계좌거래가 생성되지 않는다.")
    @Test
    void withdrawWhenFail() {
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
                .balance(100L)
                .createdAt(now)
                .updatedAt(now)
                .closedAt(null)
                .build();

        accountJpaRepository.save(accountJpaEntity);
        AccountJpaEntity beforeAccount = accountJpaRepository.findById(accountId).orElse(null);
        Objects.requireNonNull(beforeAccount);
        Long balanceBefore = beforeAccount.getBalance();

        Long amount = 1000L;
        AccountWithdrawRequest request = new AccountWithdrawRequest(amount);

        // when
        ResponseEntity<ErrorResponse> response = whenWithdrawFail(accountId, request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        AccountJpaEntity afterAccount = accountJpaRepository.findById(accountId).orElse(null);
        Objects.requireNonNull(afterAccount);
        Long balanceAfter = afterAccount.getBalance();

        assertThat(balanceAfter).isEqualTo(balanceBefore);

        List<AccountTransactionJpaEntity> accountTransaction = accountTransactionJpaRepository.findAll();
        assertThat(accountTransaction).hasSize(0);
    }

    private ResponseEntity<Void> whenWithdrawSuccess(Long accountId, AccountWithdrawRequest request) {
        return restClient.post()
                .uri("/api/v1/accounts/{accountId}/withdraw", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    private ResponseEntity<ErrorResponse> whenWithdrawFail(Long accountId, AccountWithdrawRequest request) {
        return restClient.post()
                .uri("/api/v1/accounts/{accountId}/withdraw", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange((req, res) -> {
                    ErrorResponse body = res.bodyTo(ErrorResponse.class);
                    return new ResponseEntity<>(body, res.getHeaders(), res.getStatusCode());
                });
    }
}
