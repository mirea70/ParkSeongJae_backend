package com.wirebarly.out.persistence.jpa.account.adapter;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.AccountTransaction;
import com.wirebarly.out.persistence.jpa.account.entity.AccountTransactionJpaEntity;
import com.wirebarly.out.persistence.jpa.account.repository.AccountTransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@ActiveProfiles("test")
@Import(AccountTransactionPersistenceAdapter.class)
class AccountTransactionPersistenceAdapterTest {

    @Autowired
    private AccountTransactionPersistenceAdapter accountTransactionPersistenceAdapter;

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @DisplayName("계좌거래 데이터 저장 시, 저장했던 정보가 잘 조회된다.")
    @Test
    void insert() {
        // given
        LocalDateTime now = LocalDateTime.now();

        Account account = Account.createNew(
                1L,
                2L,
                "039",
                "123123123123",
                now
        );
        Long amount = 1000L;
        Long accountTransactionId = 5L;
        AccountTransaction accountTransaction = account.deposit(amount, now, accountTransactionId);

        // when
        accountTransactionPersistenceAdapter.insert(accountTransaction);

        // then
        List<AccountTransactionJpaEntity> results = accountTransactionRepository.findAll();

        assertThat(results).hasSize(1)
                .extracting("accountTransactionId", "amount")
                .containsExactlyInAnyOrder(
                        tuple(accountTransactionId, amount)
                );
    }
}