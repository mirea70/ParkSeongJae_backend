package com.wirebarley.out.persistence.jpa.customer.repository;

import com.wirebarley.out.persistence.jpa.customer.entity.CustomerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity,Long> {

    /**
     * SELECT customer_id
     * FROM customer
     * WHERE customer_id = ?
     *   AND deleted_at IS NULL
     * Limit 1;
     */
    boolean existsByCustomerIdAndDeletedAt(Long customerId, LocalDateTime deletedAt);
}
