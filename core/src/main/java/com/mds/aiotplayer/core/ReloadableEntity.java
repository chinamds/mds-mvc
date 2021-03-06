/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

import java.io.Serializable;

/**
 * Implemented by all entities that can be reloaded by the {@link Context}.
 *
 * @param <T> type of this entity's primary key.
 * @see com.mds.aiotplayer.core.Context#reloadEntity(ReloadableEntity)
 */
public interface ReloadableEntity<T extends Serializable> {
    /**
     * The unique identifier of this entity instance.
     *
     * @return the value of the primary key for this instance.
     */
    T getId();
}
