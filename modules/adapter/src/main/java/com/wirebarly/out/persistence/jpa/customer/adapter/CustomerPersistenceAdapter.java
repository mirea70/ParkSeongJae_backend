package com.wirebarly.out.persistence.jpa.customer.adapter;

import com.wirebarly.customer.model.CustomerId;
import com.wirebarly.out.PersistenceAdapter;
import com.wirebarly.out.customer.CustomerOutPort;
import com.wirebarly.out.persistence.jpa.customer.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements CustomerOutPort {
    private final CustomerJpaRepository customerJpaRepository;

    @Override
    public boolean isExist(CustomerId id) {
        return customerJpaRepository.existsByCustomerIdAndDeletedAt(id.getValue(), null);
    }
}
