package com.wirebarley.in.web.account.response;

import com.wirebarley.in.transfer.result.TransferResult;

import java.time.LocalDateTime;
import java.util.List;

public record TransferResponse(
        Long transferId,
        String direction,
        Long counterpartyAccountId,
        Long amount,
        Long fee,
        LocalDateTime transferredAt
) {
    public static List<TransferResponse> from(List<TransferResult> results) {
        return results.stream()
                .map(TransferResponse::from)
                .toList();
    }

    public static TransferResponse from(TransferResult transferResult) {
        return new TransferResponse(
                transferResult.transferId(),
                transferResult.direction(),
                transferResult.counterpartyAccountId(),
                transferResult.amount(),
                transferResult.fee(),
                transferResult.transferredAt()
        );
    }
}
