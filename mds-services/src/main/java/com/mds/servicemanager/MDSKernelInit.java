/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.servicemanager;

import com.mds.kernel.MDSKernel;
import com.mds.kernel.MDSKernelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class simplifies the handling of lookup, registration, and
 * access of a MDS Kernel MBean.  This class has all static
 * methods.
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class MDSKernelInit {

    private static Logger log = LoggerFactory.getLogger(MDSKernelInit.class);

    private static final Object staticLock = new Object();

    /**
     * Default constructor
     */
    private MDSKernelInit() { }

    /**
     * Creates or retrieves a MDS Kernel with the given name.
     *
     * @param name kernel name (or null for default kernel)
     * @return a MDS Kernel
     * @throws IllegalStateException if the Kernel cannot be created
     */
    public static MDSKernelImpl getKernel(String name) {
        if (name != null) {
            try {
                MDSKernel kernel = new MDSKernelManager().getKernel(name);
                if (kernel != null) {
                    if (kernel instanceof MDSKernelImpl) {
                        return (MDSKernelImpl) kernel;
                    }

                    throw new IllegalStateException("Wrong MDSKernel implementation");
                }
            } catch (Exception e) {
                // Ignore exceptions here
            }
        } else if (MDSKernelManager.getDefaultKernel() != null) {
            return (MDSKernelImpl) MDSKernelManager.getDefaultKernel();
        }

        synchronized (staticLock) {
            MDSKernelImpl kernelImpl = new MDSKernelImpl(name);
            log.info("Created new kernel: " + kernelImpl);

            if (name != null) {
                MDSKernelManager.registerMBean(kernelImpl.getMBeanName(), kernelImpl);
            } else {
                MDSKernelManager.setDefaultKernel(kernelImpl);
            }

            return kernelImpl;
        }
    }
}
