package com.wirebarly.customer.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Customer {
    private final CustomerId id;
    private String name;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static Customer create(CustomerId id, String name) {
        return new Customer(
                id,
                name,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }
}
