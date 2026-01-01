package com.wirebarly.out.persistence.jpa.transfer.repository;

import com.wirebarly.out.persistence.jpa.transfer.entity.TransferJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferJpaRepository extends JpaRepository<TransferJpaEntity, Long>, TransferJpaQueryRepository {
}
