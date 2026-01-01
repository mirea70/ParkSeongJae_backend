package com.wirebarly;

import com.wirebarly.common.model.Loaded;

public class TestLoaded<T> implements Loaded<T> {
    private final T domain;

    public TestLoaded(T domain) {
        this.domain = domain;
    }

    @Override
    public T domain() {
        return domain;
    }
}
