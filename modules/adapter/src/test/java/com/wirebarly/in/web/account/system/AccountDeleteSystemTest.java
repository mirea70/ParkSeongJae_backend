package com.wirebarly.in.web.account.system;

import com.wirebarly.account.model.AccountStatus;
import com.wirebarly.in.web.error.response.ErrorResponse;
import com.wirebarly.out.persistence.jpa.account.entity.AccountJpaEntity;
import com.wirebarly.out.persistence.jpa.customer.entity.CustomerJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountDeleteSystemTest extends AccountSystemTestSupport {

    @DisplayName("계좌를 삭제하면 계좌가 해지 상태로 변경된다.")
    @Test
    void accountDelete() {
        // given
        Long accountId = 2L;
        Long customerId = 1L;
        LocalDateTime now = LocalDateTime.now();

        customerJpaRepository.save(
                new CustomerJpaEntity(customerId, "Smith", now, now, null)
        );

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

        // when
        ResponseEntity<Void> response = whenDeleteAccountSuccess(accountId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        AccountJpaEntity after = accountJpaRepository.findById(accountId).orElse(null);
        assertThat(after).isNotNull();
        assertThat(after.getStatus()).isEqualTo(AccountStatus.CLOSED.name());
        assertThat(after.getClosedAt()).isNotNull();
    }

    @DisplayName("계좌 삭제를 여러번 요청하면 최초 한번만 성공한다.")
    @Test
    void accountDeleteMultiple() {
        // given
        Long accountId = 2L;
        Long customerId = 1L;
        LocalDateTime now = LocalDateTime.now();

        customerJpaRepository.save(
                new CustomerJpaEntity(customerId, "Smith", now, now, null)
        );

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

        // when
        ResponseEntity<Void> response1 = whenDeleteAccountSuccess(accountId);
        ResponseEntity<ErrorResponse> response2 = whenDeleteAccountFail(accountId);

        // then
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);

        AccountJpaEntity after1 = accountJpaRepository.findById(accountId).orElse(null);
        assertThat(after1).isNotNull();
        assertThat(after1.getStatus()).isEqualTo(AccountStatus.CLOSED.name());
        assertThat(after1.getClosedAt()).isNotNull();

        assertThat(response2.getStatusCode().is4xxClientError()).isTrue();
        AccountJpaEntity after2 = accountJpaRepository.findById(accountId).orElse(null);
        assertThat(after2).isNotNull();
        assertThat(after2.getStatus()).isEqualTo(AccountStatus.CLOSED.name());
        assertThat(after2.getClosedAt()).isNotNull();
    }

    @DisplayName("계좌 삭제시 해당 계좌가 존재하지 않으면 실패한다.")
    @Test
    void accountDeleteWhenNotExistAccount() {
        // given
        Long accountId = 2L;

        // when
        ResponseEntity<ErrorResponse> response = whenDeleteAccountFail(accountId);

        // then
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody()).isNotNull();
    }

    @DisplayName("계좌 삭제시 해당 계좌의 소유 고객이 존재하지 않으면 실패한다.")
    @Test
    void accountDeleteWhenNotExistCustomer() {
        // given
        Long accountId = 2L;
        Long customerId = 1L;
        LocalDateTime now = LocalDateTime.now();

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

        // when
        ResponseEntity<ErrorResponse> response = whenDeleteAccountFail(accountId);

        // then
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody()).isNotNull();
    }

    private ResponseEntity<Void> whenDeleteAccountSuccess(Long accountId) {
        return  restClient.delete()
                .uri("/api/v1/accounts/{accountId}", accountId)
                .retrieve()
                .toBodilessEntity();
    }

    private ResponseEntity<ErrorResponse> whenDeleteAccountFail(Long accountId) {
        return restClient.delete()
                .uri("/api/v1/accounts/{accountId}", accountId)
                .exchange((req, res) -> {
                    ErrorResponse body = res.bodyTo(ErrorResponse.class);
                    return new ResponseEntity<>(body, res.getHeaders(), res.getStatusCode());
                });
    }
}
