package com.wirebarly.in.transfer.result;

import com.wirebarly.transfer.model.Transfer;

import java.time.LocalDateTime;

public record TransferResult(
        Long transferId,
        String direction,
        Long counterpartyAccountId,
        Long amount,
        Long fee,
        LocalDateTime transferredAt
) {
    public static TransferResult from(Transfer transfer, String direction, Long counterpartyAccountId) {
        return new TransferResult(
                transfer.getId().getValue(),
                direction,
                counterpartyAccountId,
                transfer.getAmount().getValue(),
                transfer.getFee().getValue(),
                transfer.getTransferredAt()
        );
    }
}
