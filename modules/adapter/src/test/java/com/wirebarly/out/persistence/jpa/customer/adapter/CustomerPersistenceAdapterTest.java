package com.wirebarly.out.persistence.jpa.customer.adapter;

import com.wirebarly.customer.model.CustomerId;
import com.wirebarly.out.persistence.jpa.PersistenceAdapterJpaTestSupport;
import com.wirebarly.out.persistence.jpa.customer.entity.CustomerJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerPersistenceAdapterTest extends PersistenceAdapterJpaTestSupport {

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