package com.wirebarly.out.persistence.jpa.account.entity;

import com.wirebarly.account.model.Account;
import com.wirebarly.error.exception.BusinessException;
import com.wirebarly.error.info.SystemErrorInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AccountJpaEntity {

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

    @Column(nullable = false)
    protected LocalDateTime createdAt;

    @Column(nullable = false)
    protected LocalDateTime updatedAt;

    @Column(nullable = true)
    private LocalDateTime closedAt;

    @Builder
    private AccountJpaEntity(Long accountId, Long customerId, String bankCode, String accountNumber, String status, Long balance, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime closedAt) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.bankCode = bankCode;
        this.accountNumber = accountNumber;
        this.status = status;
        this.balance = balance;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.closedAt = closedAt;
    }

    public static AccountJpaEntity from(Account account) {
        return AccountJpaEntity.builder()
                .accountId(account.getId().getValue())
                .customerId(account.getCustomerId().getValue())
                .bankCode(account.getBankInfo().getBankCode().getCode())
                .accountNumber(account.getBankInfo().getAccountNumber().getValue())
                .status(account.getStatus().name())
                .balance(account.getBalance().getValue())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .closedAt(account.getClosedAt())
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

    public void updateFrom(Account domain) {
        if (domain == null || domain.getId() == null) {
            throw new BusinessException(SystemErrorInfo.INTERNAL_SERVER_ERROR, Map.of("cause", "도메인 객체가 유실되는 문제가 발생했습니다."));
        }

        this.bankCode = domain.getBankInfo().getBankCode().getCode();
        this.accountNumber = domain.getBankInfo().getAccountNumber().getValue();
        this.status = domain.getStatus().name();
        this.balance = domain.getBalance().getValue();
        this.updatedAt = domain.getUpdatedAt();
        this.closedAt = domain.getClosedAt();
    }

    public void applyCloseFrom(Account domain) {
        this.status = domain.getStatus().name();
        this.updatedAt = domain.getUpdatedAt();
        this.closedAt = domain.getClosedAt();
    }

    public void applyBalance(Account domain) {
        this.balance = domain.getBalance().getValue();
        this.updatedAt = domain.getUpdatedAt();
    }
}
