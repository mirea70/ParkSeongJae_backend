package com.wirebarly.out.persistence.jpa.customer.entity;

import com.wirebarly.out.persistence.jpa.common.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CustomerJpaEntity extends BaseJpaEntity {

    @Id
    private Long customerId;

    @Column(nullable = false)
    private String name;

    private LocalDateTime deletedAt;

    @Builder
    public CustomerJpaEntity(Long customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }
}
