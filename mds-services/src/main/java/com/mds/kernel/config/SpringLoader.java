/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.kernel.config;

import com.mds.services.ConfigurationService;

/**
 * Interface that is used so that modules can determine their own spring file locations
 *
 * @author Kevin Van de Velde (kevin at atmire dot com)
 */
public interface SpringLoader {

    public final String XML_SUFFIX = "*.xml";

    /**
     * Returns all the locations that contain spring files
     *
     * @param configurationService the mds configuration service
     * @return an array containing spring file locations
     */
    public String[] getResourcePaths(ConfigurationService configurationService);

}
