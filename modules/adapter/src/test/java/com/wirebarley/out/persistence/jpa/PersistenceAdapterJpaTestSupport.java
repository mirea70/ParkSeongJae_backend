package com.wirebarley.out.persistence.jpa;

import com.wirebarley.out.persistence.jpa.account.adapter.AccountPersistenceAdapter;
import com.wirebarley.out.persistence.jpa.account.adapter.AccountTransactionPersistenceAdapter;
import com.wirebarley.out.persistence.jpa.account.repository.AccountJpaRepository;
import com.wirebarley.out.persistence.jpa.account.repository.AccountTransactionJpaRepository;
import com.wirebarley.out.persistence.jpa.config.JpaConfig;
import com.wirebarley.out.persistence.jpa.customer.adapter.CustomerPersistenceAdapter;
import com.wirebarley.out.persistence.jpa.customer.repository.CustomerJpaRepository;
import com.wirebarley.out.persistence.jpa.transfer.adapter.TransferPersistenceAdapter;
import com.wirebarley.out.persistence.jpa.transfer.repository.TransferJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({
        AccountPersistenceAdapter.class,
        AccountTransactionPersistenceAdapter.class,
        CustomerPersistenceAdapter.class,
        TransferPersistenceAdapter.class,
        JpaConfig.class
})
public abstract class PersistenceAdapterJpaTestSupport {

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    protected AccountPersistenceAdapter accountPersistenceAdapter;

    @Autowired
    protected AccountTransactionPersistenceAdapter accountTransactionPersistenceAdapter;

    @Autowired
    protected CustomerPersistenceAdapter customerPersistenceAdapter;

    @Autowired
    protected TransferPersistenceAdapter transferPersistenceAdapter;

    @Autowired
    protected AccountJpaRepository accountJpaRepository;

    @Autowired
    protected AccountTransactionJpaRepository accountTransactionJpaRepository;

    @Autowired
    protected CustomerJpaRepository customerJpaRepository;

    @Autowired
    protected TransferJpaRepository transferJpaRepository;
}
