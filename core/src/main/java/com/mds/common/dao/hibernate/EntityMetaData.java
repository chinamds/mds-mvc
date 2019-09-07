package com.mds.common.dao.hibernate;

import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryBuilderFactory;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;

public class EntityMetaData implements SessionFactoryBuilderFactory {

    private static final ThreadLocal<MetadataImplementor> meta = new ThreadLocal<>();

    @Override
    public SessionFactoryBuilder getSessionFactoryBuilder(MetadataImplementor metadata, SessionFactoryBuilderImplementor defaultBuilder) {
        meta.set(metadata);
        return defaultBuilder;
    }

    public static MetadataImplementor getMeta() {
        return meta.get();
    }
}