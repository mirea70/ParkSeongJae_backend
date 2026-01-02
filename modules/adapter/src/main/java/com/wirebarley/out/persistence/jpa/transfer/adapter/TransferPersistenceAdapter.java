package com.wirebarley.out.persistence.jpa.transfer.adapter;

import com.wirebarley.account.model.AccountId;
import com.wirebarley.in.transfer.result.TransferResult;
import com.wirebarley.out.PersistenceAdapter;
import com.wirebarley.out.persistence.jpa.transfer.entity.TransferJpaEntity;
import com.wirebarley.out.persistence.jpa.transfer.repository.TransferJpaRepository;
import com.wirebarley.out.transfer.TransferOutPort;
import com.wirebarley.transfer.model.Transfer;
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
