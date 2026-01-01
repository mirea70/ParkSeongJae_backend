package com.wirebarly.out.persistence.jpa.transfer.entity;

import com.wirebarly.transfer.model.Transfer;
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
@Table(name = "transfer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TransferJpaEntity {

    @Id
    private Long transferId;

    @Column(nullable = false)
    private Long fromAccountId;

    @Column(nullable = false)
    private Long toAccountId;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long fee;

    @Column(nullable = false)
    private LocalDateTime transferredAt;

    @Builder
    private TransferJpaEntity(Long transferId, Long fromAccountId, Long toAccountId, Long amount, Long fee, LocalDateTime transferredAt) {
        this.transferId = transferId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.fee = fee;
        this.transferredAt = transferredAt;
    }

    public static TransferJpaEntity from(Transfer transfer) {
        return TransferJpaEntity.builder()
                .transferId(transfer.getId().getValue())
                .fromAccountId(transfer.getFromAccountId().getValue())
                .toAccountId(transfer.getToAccountId().getValue())
                .amount(transfer.getAmount().getValue())
                .fee(transfer.getFee().getValue())
                .transferredAt(transfer.getTransferredAt())
                .build();
    }
}
