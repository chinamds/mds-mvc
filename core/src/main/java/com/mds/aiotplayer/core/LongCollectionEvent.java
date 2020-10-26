/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

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

