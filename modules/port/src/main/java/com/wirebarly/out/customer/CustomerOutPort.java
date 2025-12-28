package com.wirebarly.out.customer;

import com.wirebarly.customer.model.CustomerId;

public interface CustomerOutPort {
    boolean isExist(CustomerId id);
}
