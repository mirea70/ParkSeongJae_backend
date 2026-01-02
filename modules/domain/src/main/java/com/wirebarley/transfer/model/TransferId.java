package com.wirebarley.transfer.model;

import com.wirebarley.error.exception.DomainException;
import com.wirebarley.error.info.TransferErrorInfo;
import lombok.Getter;

@Getter
public class TransferId {
    private final long value;

    public TransferId(Long input) {
        validate(input);
        this.value = input;
    }

    private void validate(Long input) {
        if (input == null)
            throw new DomainException(TransferErrorInfo.ID_NOT_EXIST);
        if (input <= 0) {
            throw new DomainException(TransferErrorInfo.ID_NOT_POSITIVE);
        }
    }

}
