/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.kernel;

import com.mds.services.ConfigurationService;

/**
 * This is the most core piece of the system:  instantiating one will
 * startup the mds services framework.
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public interface MDSKernel extends CommonLifecycle<MDSKernel> {

    public static final String KERNEL_NAME = "Kernel";
    public static final String MBEAN_PREFIX = "com.mds:name=";
    public static final String MBEAN_SUFFIX = ",type=MDSKernel";
    public static final String MBEAN_NAME = MBEAN_PREFIX + KERNEL_NAME + MBEAN_SUFFIX;

    /**
     * @return the unique MBean name of this MDS Kernel
     */
    public String getMBeanName();

    /**
     * @return true if this Kernel is started and running
     */
    public boolean isRunning();

    /**
     * @return the MDS service manager instance for this Kernel
     */
    public ServiceManager getServiceManager();

    /**
     * @return the MDS configuration service for this Kernel
     */
    public ConfigurationService getConfigurationService();

}
