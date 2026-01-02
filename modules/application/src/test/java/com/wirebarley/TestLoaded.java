package com.wirebarley;

import com.wirebarley.common.model.Loaded;

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
