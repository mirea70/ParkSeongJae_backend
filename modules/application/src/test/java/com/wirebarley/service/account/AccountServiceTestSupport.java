package com.wirebarley.service.account;

import com.wirebarley.ServiceTestSupport;
import com.wirebarley.out.account.AccountOutPort;
import com.wirebarley.out.account.AccountTransactionOutPort;
import com.wirebarley.out.customer.CustomerOutPort;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class AccountServiceTestSupport extends ServiceTestSupport {
    @InjectMocks
    protected AccountService accountService;

    @Mock
    protected CustomerOutPort customerOutPort;

    @Mock
    protected AccountOutPort accountOutPort;

    @Mock
    protected AccountTransactionOutPort accountTransactionOutPort;
}
