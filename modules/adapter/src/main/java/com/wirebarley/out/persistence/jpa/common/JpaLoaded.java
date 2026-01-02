package com.wirebarley.out.persistence.jpa.common;

import com.wirebarley.common.model.Loaded;

public final class JpaLoaded<D, E> implements Loaded<D> {
    private final D domain;
    private final E entity;

    public JpaLoaded(D domain, E entity) {
        this.domain = domain;
        this.entity = entity;
    }

    @Override
    public D domain() {
        return domain;
    }

    public E entity() {
        return entity;
    }
}
