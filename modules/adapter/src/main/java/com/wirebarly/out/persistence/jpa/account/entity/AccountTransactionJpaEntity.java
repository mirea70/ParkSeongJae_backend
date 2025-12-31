package com.wirebarly.out.persistence.jpa.account.entity;

import com.wirebarly.account.model.AccountTransaction;
import com.wirebarly.transfer.model.TransferId;
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
@Table(name = "account_transactions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AccountTransactionJpaEntity {

    @Id
    private Long accountTransactionId;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = true)
    private Long transferId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long balanceAfter;

    @Column(nullable = false)
    private LocalDateTime transactedAt;

    @Builder
    public AccountTransactionJpaEntity(Long accountTransactionId, Long accountId, Long transferId, String type, Long amount, Long balanceAfter, LocalDateTime transactedAt) {
        this.accountTransactionId = accountTransactionId;
        this.accountId = accountId;
        this.transferId = transferId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.transactedAt = transactedAt;
    }

    public static AccountTransactionJpaEntity from(AccountTransaction accountTransaction) {
        TransferId transferId = accountTransaction.getTransferId();

        return AccountTransactionJpaEntity.builder()
                .accountTransactionId(accountTransaction.getId().getValue())
                .accountId(accountTransaction.getAccountId().getValue())
                .transferId(transferId != null ? transferId.getValue() : null)
                .type(accountTransaction.getType().name())
                .amount(accountTransaction.getAmount().getValue())
                .balanceAfter(accountTransaction.getBalanceAfter().getValue())
                .transactedAt(accountTransaction.getTransactedAt())
                .build();
    }
}
