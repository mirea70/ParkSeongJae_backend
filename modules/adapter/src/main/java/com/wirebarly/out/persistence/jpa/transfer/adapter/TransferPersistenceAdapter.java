package com.wirebarly.out.persistence.jpa.transfer.adapter;

import com.wirebarly.account.model.AccountId;
import com.wirebarly.in.transfer.result.TransferResult;
import com.wirebarly.out.PersistenceAdapter;
import com.wirebarly.out.persistence.jpa.transfer.entity.TransferJpaEntity;
import com.wirebarly.out.persistence.jpa.transfer.repository.TransferJpaRepository;
import com.wirebarly.out.transfer.TransferOutPort;
import com.wirebarly.transfer.model.Transfer;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@PersistenceAdapter
@RequiredArgsConstructor
public class TransferPersistenceAdapter implements TransferOutPort {
    private final TransferJpaRepository transferJpaRepository;
    private final EntityManager entityManager;

    @Override
    public void insert(Transfer transfer) {
        entityManager.persist(TransferJpaEntity.from(transfer));
    }

    @Override
    public Long getDailyTransferAmount(AccountId fromAccountId, LocalDate today) {
        LocalDateTime from = today.atStartOfDay();
        LocalDateTime to = from.plusDays(1);

        return transferJpaRepository.getSumByFromAccountBetween(fromAccountId.getValue(), from, to);
    }

    @Override
    public List<TransferResult> getTransfersBy(AccountId accountId) {
        return transferJpaRepository.findAllByAccountId(accountId.getValue());
    }
}
