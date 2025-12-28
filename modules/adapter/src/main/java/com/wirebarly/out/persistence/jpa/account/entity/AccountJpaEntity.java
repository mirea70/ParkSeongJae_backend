package com.wirebarly.out.persistence.jpa.account.entity;

import com.wirebarly.account.model.Account;
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
@Table(name = "account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AccountJpaEntity extends BaseJpaEntity {

    @Id
    private Long accountId;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String bankCode;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Long balance;

    private LocalDateTime closedAt;

    @Builder
    private AccountJpaEntity(Long accountId, Long customerId, String bankCode, String accountNumber, String status, Long balance) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.bankCode = bankCode;
        this.accountNumber = accountNumber;
        this.status = status;
        this.balance = balance;
    }

    public static AccountJpaEntity from(Account account) {
        return AccountJpaEntity.builder()
                .accountId(account.getId().getValue())
                .customerId(account.getCustomerId().getValue())
                .bankCode(account.getBankInfo().getBankCode().getCode())
                .accountNumber(account.getBankInfo().getAccountNumber().getValue())
                .status(account.getStatus().name())
                .balance(account.getBalance().getValue())
                .build();
    }

    public Account toDomain() {
        return Account.fromOutside(
                accountId,
                customerId,
                bankCode,
                accountNumber,
                status,
                balance,
                createdAt,
                updatedAt,
                closedAt
        );
    }
}
