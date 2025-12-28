package com.wirebarly.account.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountStatus {
    ACTIVE("활성 상태"),
    CLOSED("해지 상태");

    private final String description;
}
