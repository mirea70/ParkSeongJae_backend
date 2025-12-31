package com.wirebarly.out.persistence.jpa.customer.adapter;

import com.wirebarly.customer.model.CustomerId;
import com.wirebarly.out.persistence.jpa.customer.entity.CustomerJpaEntity;
import com.wirebarly.out.persistence.jpa.customer.repository.CustomerJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(CustomerPersistenceAdapter.class)
class CustomerPersistenceAdapterTest {

    @Autowired
    private CustomerPersistenceAdapter customerPersistenceAdapter;
    @Autowired
    private CustomerJpaRepository customerJpaRepository;

    @DisplayName("고객이 DB에 저장되어 있으면 true를 반환한다.")
    @Test
    void isExistWhenHave() {
        // given
        CustomerId customerId = new CustomerId(1L);
        LocalDateTime now = LocalDateTime.now();

        customerJpaRepository.save(
                CustomerJpaEntity.builder()
                        .customerId(customerId.getValue())
                        .createdAt(now)
                        .updatedAt(now)
                        .name("name")
                        .build()
        );

        // when
        boolean result = customerPersistenceAdapter.isExist(customerId);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("고객이 DB에 저장되어 있지않으면 false를 반환한다.")
    @Test
    void isExistWhenDontHave() {
        // given
        CustomerId customerId = new CustomerId(1L);

        // when
        boolean result = customerPersistenceAdapter.isExist(customerId);

        // then
        assertThat(result).isFalse();
    }
}