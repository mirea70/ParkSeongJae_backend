package com.wirebarley.out.customer;

import com.wirebarley.customer.model.CustomerId;

public interface CustomerOutPort {
    boolean isExist(CustomerId id);
}
