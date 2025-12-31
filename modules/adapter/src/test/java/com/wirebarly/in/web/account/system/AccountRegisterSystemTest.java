package com.wirebarly.in.web.account.system;

import com.wirebarly.in.web.account.request.AccountCreateRequest;
import com.wirebarly.in.web.account.response.AccountResponse;
import com.wirebarly.in.web.error.response.ErrorResponse;
import com.wirebarly.out.persistence.jpa.account.entity.AccountJpaEntity;
import com.wirebarly.out.persistence.jpa.customer.entity.CustomerJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class AccountRegisterSystemTest extends AccountSystemTestSupport {

    @DisplayName("계좌 등록: 성공 테스트")
    @Test
    void accountRegister() {
        // given
        LocalDateTime now = LocalDateTime.now();
        customerJpaRepository.save(
                new CustomerJpaEntity(1L, "Smith", now, now ,null)
        );

        Long customerId = 1L;
        String bankCode = "039";
        String accountNumber = "11111111111";

        AccountCreateRequest request = new AccountCreateRequest(
                customerId,
                bankCode,
                accountNumber
        );

        // when
        ResponseEntity<AccountResponse> response = whenRegisterAccountSuccess(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        List<AccountJpaEntity> accounts = accountJpaRepository.findAll();
        assertThat(accounts).hasSize(1)
                .extracting("customerId", "bankCode", "accountNumber")
                .containsExactlyInAnyOrder(
                        tuple(customerId, bankCode, accountNumber)
                );
    }

    @DisplayName("계좌 등록: 입력값 실패 테스트")
    @Test
    void accountRegisterValidationFail() {
        // given
        AccountCreateRequest request = new AccountCreateRequest(
                1L,
                "039",
                ""
        );

        // when
        ResponseEntity<ErrorResponse> response = whenRegisterAccountFail(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("INVALID_INPUT_VALUE");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/accounts/new");
    }

    @DisplayName("계좌 등록: 비즈니스 실패 테스트")
    @Test
    void accountRegisterBusinessFail() {
        // given
        AccountCreateRequest request = new AccountCreateRequest(
                1L,
                "039",
                "11111111111"
        );

        // when
        ResponseEntity<ErrorResponse> response = whenRegisterAccountFail(request);

        // then
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/accounts/new");

        long accountCount = accountJpaRepository.count();
        assertThat(accountCount).isEqualTo(0);
    }

    private ResponseEntity<AccountResponse> whenRegisterAccountSuccess(AccountCreateRequest request) {
        return restClient.post()
                .uri("/api/v1/accounts/new")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(AccountResponse.class);
    }

    private ResponseEntity<ErrorResponse> whenRegisterAccountFail(AccountCreateRequest request) {
        return restClient.post()
                .uri("/api/v1/accounts/new")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange((req, res) -> {
                    ErrorResponse body = res.bodyTo(ErrorResponse.class);
                    return new ResponseEntity<>(body, res.getHeaders(), res.getStatusCode());
                });
    }
}
