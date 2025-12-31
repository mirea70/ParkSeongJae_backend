package com.wirebarly.service.account;

import com.wirebarly.ServiceTestSupport;
import com.wirebarly.out.account.AccountOutPort;
import com.wirebarly.out.account.AccountTransactionOutPort;
import com.wirebarly.out.customer.CustomerOutPort;
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
