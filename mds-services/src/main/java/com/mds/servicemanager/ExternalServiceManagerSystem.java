/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.servicemanager;

import java.util.List;

import com.mds.servicemanager.config.MDSConfigurationService;

/**
 * Interface for modular service manager systems.
 * Provides a generic initialization routine, in lieu of hardcoded
 * constructors.
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public interface ExternalServiceManagerSystem extends ServiceManagerSystem {
    /**
     * Initialize the service manager's configuration.
     *
     * @param parent               parent ServiceManagerSystem
     * @param configurationService current MDS configuration service
     * @param testMode             whether in test mode
     * @param developmentMode      whether in development mode
     * @param serviceManagers      List of ServiceManagerSystems
     */
    void init(ServiceManagerSystem parent, MDSConfigurationService configurationService,
              boolean testMode, boolean developmentMode, List<ServiceManagerSystem> serviceManagers);

}
