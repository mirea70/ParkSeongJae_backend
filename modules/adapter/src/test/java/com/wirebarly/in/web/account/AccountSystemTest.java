package com.wirebarly.in.web.account;

import com.wirebarly.in.web.account.request.AccountCreateRequest;
import com.wirebarly.in.web.account.response.AccountResponse;
import com.wirebarly.in.web.error.response.ErrorResponse;
import com.wirebarly.out.persistence.jpa.account.entity.AccountJpaEntity;
import com.wirebarly.out.persistence.jpa.account.repository.AccountJpaRepository;
import com.wirebarly.out.persistence.jpa.customer.entity.CustomerJpaEntity;
import com.wirebarly.out.persistence.jpa.customer.repository.CustomerJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AccountSystemTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private CustomerJpaRepository customerJpaRepository;

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    @BeforeEach
    public void setup() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @AfterEach
    public void tearDown() {
        customerJpaRepository.deleteAllInBatch();
        accountJpaRepository.deleteAllInBatch();
    }

    @DisplayName("계좌 등록: 성공 테스트")
    @Test
    void accountRegister() {
        // given
        customerJpaRepository.save(
                new CustomerJpaEntity(1L, "Smith")
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

        List<AccountJpaEntity> accounts = accountJpaRepository.findAll();
        assertThat(accounts).hasSize(0);
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
