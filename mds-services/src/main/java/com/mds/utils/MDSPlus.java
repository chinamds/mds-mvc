/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.utils;

import com.mds.kernel.MDSKernel;
import com.mds.kernel.MDSKernelManager;
import com.mds.kernel.ServiceManager;
import com.mds.services.ConfigurationService;
import com.mds.services.EventService;
import com.mds.services.RequestService;


/**
 * This is the MDS helper services access object.
 * It allows access to all core MDS services for those who cannot or
 * will not use the injection service to get services.
 * <p>
 * Note that this may not include every core service but should include
 * all the services that are useful to UI developers at least.
 * <p>
 * This should be initialized using the constructor and then can be used
 * as long as the kernel is not shutdown.  Making multiple copies of
 * this is cheap and can be done without worry about the cost.
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public final class MDSPlus {

    private MDSKernel kernel;

    public MDSKernel getKernel() {
        return kernel;
    }

    /**
     * Construct a MDS helper object which uses the default kernel.
     *
     * @throws IllegalStateException if the kernel is not already running
     */
    public MDSPlus() {
        this(null);
    }

    /**
     * Construct a MDS helper object which uses the a specific named
     * instance of the kernel.
     *
     * @param kernelName the name of the kernel to use (null to use the default kernel)
     * @throws IllegalStateException if the kernel is not already running or no kernel exists with this name
     */
    public MDSPlus(String kernelName) {
        MDSKernel kernel = new MDSKernelManager().getKernel(kernelName);
        this.kernel = kernel;
    }

    public ServiceManager getServiceManager() {
        if (kernel == null) {
            throw new IllegalStateException("MDS kernel cannot be null");
        }
        return kernel.getServiceManager();
    }

    // place methods to retrieve key services below here -AZ

    public ConfigurationService getConfigurationService() {
        return getServiceManager().getServiceByName(ConfigurationService.class.getName(), ConfigurationService.class);
    }

    public EventService getEventService() {
        return getServiceManager().getServiceByName(EventService.class.getName(), EventService.class);
    }

    public RequestService getRequestService() {
        return getServiceManager().getServiceByName(RequestService.class.getName(), RequestService.class);
    }

    public <T> T getSingletonService(Class<T> type) {
        return getServiceManager().getServiceByName(type.getName(), type);
    }

}
