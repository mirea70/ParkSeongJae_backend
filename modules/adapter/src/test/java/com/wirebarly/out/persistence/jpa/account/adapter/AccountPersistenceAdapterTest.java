package com.wirebarly.out.persistence.jpa.account.adapter;

import com.wirebarly.account.model.Account;
import com.wirebarly.account.model.BankCode;
import com.wirebarly.out.persistence.jpa.account.entity.AccountJpaEntity;
import com.wirebarly.out.persistence.jpa.account.repository.AccountJpaRepository;
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
@Import(AccountPersistenceAdapter.class)
class AccountPersistenceAdapterTest {

    @Autowired
    private AccountPersistenceAdapter accountPersistenceAdapter;

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    @DisplayName("계좌 데이터 저장 시, 저장했던 정보가 잘 조회된다.")
    @Test
    void save() {
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
        accountPersistenceAdapter.save(account);

        // then
        List<AccountJpaEntity> results = accountJpaRepository.findAll();

        assertThat(results).hasSize(1)
                .extracting("accountId", "accountNumber")
                .containsExactlyInAnyOrder(
                        tuple(accountId, accountNumber)
                );
    }
}