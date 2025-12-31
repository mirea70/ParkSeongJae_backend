package com.wirebarly.out.persistence.jpa.account.adapter;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.AccountId;
import com.wirebarly.account.model.BankCode;
import com.wirebarly.out.persistence.jpa.account.entity.AccountJpaEntity;
import com.wirebarly.out.persistence.jpa.account.repository.AccountJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@ActiveProfiles("test")
@Import(AccountPersistenceAdapter.class)
class AccountPersistenceAdapterTest {

    @Autowired
    private AccountPersistenceAdapter accountPersistenceAdapter;

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    @Autowired
    private EntityManager entityManager;

    @DisplayName("계좌 데이터 저장 시, 저장했던 정보가 잘 조회된다.")
    @Test
    void insert() {
        // given
        Long accountId = 1L;
        LocalDateTime now = LocalDateTime.now();
        String accountNumber = "1231123123123";

        Account account = Account.createNew(
                accountId,
                2L,
                BankCode.IBK.getCode(),
                accountNumber,
                now
        );

        // when
        accountPersistenceAdapter.insert(account);

        // then
        List<AccountJpaEntity> results = accountJpaRepository.findAll();

        assertThat(results).hasSize(1)
                .extracting("accountId", "accountNumber")
                .containsExactlyInAnyOrder(
                        tuple(accountId, accountNumber)
                );
    }

    @DisplayName("계좌 데이터가 존재하면 도메인 데이터로 변환해 반환한다.")
    @Test
    void loadOne() {
        // given
        AccountId accountId = new AccountId(1L);
        Long customerId = 2L;
        String bankCode = "039";
        String accountNumber = "1231123123123";
        String status = "ACTIVE";
        Long balance = 0L;
        LocalDateTime now = LocalDateTime.now();

        AccountJpaEntity accountJpaEntity = AccountJpaEntity.builder()
                .accountId(accountId.getValue())
                .customerId(customerId)
                .bankCode(bankCode)
                .accountNumber(accountNumber)
                .status(status)
                .balance(balance)
                .createdAt(now)
                .updatedAt(now)
                .closedAt(null)
                .build();

        entityManager.persist(accountJpaEntity);

        // when
        Optional<Account> result = accountPersistenceAdapter.loadOne(accountId);

        // then
        assertThat(result).isPresent();

        Account account = result.get();
        assertThat(account.getId().getValue()).isEqualTo(accountId.getValue());
        assertThat(account.getBankInfo().getBankCode().getCode()).isEqualTo(bankCode);
        assertThat(account.getBankInfo().getAccountNumber().getValue()).isEqualTo(accountNumber);
    }

    @DisplayName("계좌 데이터가 해지상태이면 로드되지 않는다.")
    @ParameterizedTest
    @CsvSource(value = {
            "ACTIVE, 2025-03-05T14:32:18.456789",
            "CLOSED, null",
            "CLOSED, 2025-03-05T14:32:18.456789",
    }, nullValues = {"null"})
    void loadOneOnlyNotClosed(String status, LocalDateTime closedAt) {
        // given
        AccountId accountId = new AccountId(1L);
        Long customerId = 2L;
        String bankCode = "039";
        String accountNumber = "1231123123123";
        Long balance = 0L;
        LocalDateTime now = LocalDateTime.now();

        AccountJpaEntity accountJpaEntity = AccountJpaEntity.builder()
                .accountId(accountId.getValue())
                .customerId(customerId)
                .bankCode(bankCode)
                .accountNumber(accountNumber)
                .status(status)
                .balance(balance)
                .createdAt(now)
                .updatedAt(now)
                .closedAt(closedAt)
                .build();

        entityManager.persist(accountJpaEntity);

        // when
        Optional<Account> result = accountPersistenceAdapter.loadOne(accountId);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("계좌 데이터의 업데이트 내용이 배제해야할 컬럼 제외하고, 잘 반영된다.")
    @Test
    void update() {
        // given
        Long accountId = 1L;
        Long customerId = 2L;
        String bankCode = "039";
        String accountNumber = "1231123123123";
        String status = "ACTIVE";
        Long balance = 0L;
        LocalDateTime now = LocalDateTime.now();

        Long afterCustomerId = 5L;
        String afterBankCode = "003";
        String afterAccountNumber = "11111111111";
        String afterStatus = "CLOSED";
        Long afterBalance = 1000L;
        LocalDateTime afterCreatedAt = LocalDateTime.of(2025, 3, 5, 7, 5);
        LocalDateTime afterUpdatedAt = LocalDateTime.of(2025, 3, 5, 7, 5);
        LocalDateTime afterClosedAt = LocalDateTime.of(2025, 3, 5, 7, 5);

        Account afterAccount = Account.fromOutside(
                accountId,
                afterCustomerId,
                afterBankCode,
                afterAccountNumber,
                afterStatus,
                afterBalance,
                afterCreatedAt,
                afterUpdatedAt,
                afterClosedAt
        );

        AccountJpaEntity accountJpaEntity = AccountJpaEntity.builder()
                .accountId(accountId)
                .customerId(customerId)
                .bankCode(bankCode)
                .accountNumber(accountNumber)
                .status(status)
                .balance(balance)
                .createdAt(now)
                .updatedAt(now)
                .closedAt(null)
                .build();

        entityManager.persist(accountJpaEntity);

        // when
        accountPersistenceAdapter.update(afterAccount);
        entityManager.flush();
        entityManager.clear();

        // then
        AccountJpaEntity after = accountJpaRepository.findById(accountId).orElse(null);
        assertThat(after).isNotNull();

        assertThat(after.getCustomerId()).isNotEqualTo(afterCustomerId);
        assertThat(after.getCreatedAt()).isNotEqualTo(afterCreatedAt);

        assertThat(after.getBankCode()).isEqualTo(afterBankCode);
        assertThat(after.getAccountNumber()).isEqualTo(afterAccountNumber);
        assertThat(after.getStatus()).isEqualTo(afterStatus);
        assertThat(after.getBalance()).isEqualTo(afterBalance);
        assertThat(after.getUpdatedAt()).isEqualTo(afterUpdatedAt);
        assertThat(after.getClosedAt()).isEqualTo(afterClosedAt);
    }
}