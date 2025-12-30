package com.wirebarly.in.web.account.system;

import com.wirebarly.in.web.SystemTestSupport;
import com.wirebarly.out.persistence.jpa.account.repository.AccountJpaRepository;
import com.wirebarly.out.persistence.jpa.customer.repository.CustomerJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountSystemTestSupport extends SystemTestSupport {

    @Autowired
    protected CustomerJpaRepository customerJpaRepository;

    @Autowired
    protected AccountJpaRepository accountJpaRepository;

    @AfterEach
    public void tearDown() {
        customerJpaRepository.deleteAllInBatch();
        accountJpaRepository.deleteAllInBatch();
    }

}
