package com.mds.core;

import java.util.EventObject;
import java.util.Objects;

/// <summary>
/// Provides functionality for creating and saving the files associated with gallery objects.
/// </summary>
public class LongCollectionEvent extends EventObject {
    private final LongCollection longCollection;

    public LongCollectionEvent(Object source, LongCollection longCollection) {
        super(source);
        this.longCollection = Objects.requireNonNull(longCollection);
    }

    public LongCollection getLongCollection() {
        return this.longCollection;
    }
}

