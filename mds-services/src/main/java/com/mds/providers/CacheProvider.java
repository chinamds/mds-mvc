/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://github.com/chinamds/license/
 */
package com.mds.providers;

import java.util.List;

import com.mds.services.model.Cache;
import com.mds.services.model.CacheConfig;


/**
 * This is a provider (pluggable functionality) for MDS.
 * <p>
 * This allows an external system to define how caches are handled in
 * MDS by implementing this interface and registering it with the
 * service manager.
 * </p>
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public interface CacheProvider {

    /**
     * Gets all the caches that this provider knows about.
     *
     * @return a list of all the caches which the caching service knows about
     */
    public List<Cache> getCaches();

    /**
     * Construct a {@link Cache} with the given name (must be unique) OR
     * retrieve the one that already exists with this name.
     * <p>
     * NOTE: providers will never be asked to provide request caches
     * (e.g. {@link com.mds.services.model.CacheConfig.CacheScope#REQUEST})
     *
     * @param cacheName the unique name for this cache (e.g. com.mds.user.UserCache)
     * @param config    (optional) a configuration object.  The cache
     *                  should adhere to the settings in it.  If it is null then just use
     *                  defaults.
     * @return a cache which can be used to store serializable objects
     * @throws IllegalArgumentException if the cache name is already in use or the config is invalid
     */
    public Cache getCache(String cacheName, CacheConfig config);

    /**
     * Flush and destroy the cache with this name.
     * If the cache does not exist then this does nothing (should not
     * fail if the cache does not exist).
     *
     * @param cacheName the unique name for this cache (e.g. com.mds.user.UserCache)
     */
    public void destroyCache(String cacheName);

    /**
     * Clears the contents of all caches managed by this provider
     */
    public void resetCaches();

}
