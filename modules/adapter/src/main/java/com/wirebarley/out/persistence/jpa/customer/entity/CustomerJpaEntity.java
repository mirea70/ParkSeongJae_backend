package com.wirebarley.out.persistence.jpa.customer.entity;

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
public class CustomerJpaEntity {

    @Id
    private Long customerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updateAt;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    @Builder
    public CustomerJpaEntity(Long customerId, String name, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.customerId = customerId;
        this.name = name;
        this.createdAt = createdAt;
        this.updateAt = updatedAt;
        this.deletedAt = deletedAt;
    }
}
