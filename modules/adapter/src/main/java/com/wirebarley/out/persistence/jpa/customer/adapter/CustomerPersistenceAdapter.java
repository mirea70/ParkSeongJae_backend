package com.wirebarley.out.persistence.jpa.customer.adapter;

import com.wirebarley.customer.model.CustomerId;
import com.wirebarley.out.PersistenceAdapter;
import com.wirebarley.out.customer.CustomerOutPort;
import com.wirebarley.out.persistence.jpa.customer.repository.CustomerJpaRepository;
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
