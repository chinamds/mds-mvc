/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.kernel;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Allows the MDS kernel to be accessed if desired.
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public final class MDSKernelManager {
    private static Logger log = LoggerFactory.getLogger(MDSKernelManager.class);

    private static MDSKernel defaultKernel = null;

    private static Map<String, MDSKernel> namedKernelMap = new HashMap<String, MDSKernel>();


    public static MDSKernel getDefaultKernel() {
        return defaultKernel;
    }

    public static void setDefaultKernel(MDSKernel kernel) {
        defaultKernel = kernel;
    }

    /**
     * A lock on the kernel to handle multiple threads getting the first item.
     */
    private Object lock = new Object();

    /**
     * Get the kernel.  This will be a single instance for the JVM, but
     * the method will retrieve the same instance regardless of this
     * object instance.
     *
     * @return the MDS kernel
     * @throws IllegalStateException if the kernel is not available
     */
    public MDSKernel getKernel() {
        MDSKernel kernel = getKernel(null);
        if (kernel == null) {
            throw new IllegalStateException(
                "The MDS kernel is not started yet, please start it before attempting to use it");
        }

        return kernel;
    }

    /**
     * Get the kernel.  This will be a single instance for the JVM, but
     * the method will retrieve the same instance regardless of this
     * object instance.
     *
     * @param name this is the name of this kernel instance.  If you do
     *             not know what this is then use null.
     * @return the MDS kernel
     * @throws IllegalStateException if the kernel is not available or not running
     */
    public MDSKernel getKernel(String name) {

        // Are we getting a named kernel?
        if (!StringUtils.isEmpty(name)) {
            String checkedName = checkName(name);

            if (namedKernelMap.containsKey(checkedName)) {
                return namedKernelMap.get(checkedName);
            }

            if (defaultKernel != null && checkedName.equals(defaultKernel.getMBeanName())) {
                return defaultKernel;
            }

            synchronized (lock) {
                MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                try {
                    ObjectName kernelName = new ObjectName(checkedName);
                    MDSKernel namedKernel = (MDSKernel) mbs.invoke(kernelName, "getManagedBean", null, null);
                    if (namedKernel == null || !namedKernel.isRunning()) {
                        throw new IllegalStateException(
                            "The MDS kernel is not started yet, please start it before attempting to use it");
                    }

                    namedKernelMap.put(checkedName, namedKernel);
                    return namedKernel;
                } catch (InstanceNotFoundException e) {
                    throw new IllegalStateException(e);
                } catch (MBeanException e) {
                    throw new IllegalStateException(e);
                } catch (ReflectionException e) {
                    throw new IllegalStateException(e);
                } catch (MalformedObjectNameException e) {
                    throw new IllegalStateException(e);
                } catch (NullPointerException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        return defaultKernel;
    }

    /**
     * Static initialized random default Kernel name
     */
    private static String defaultKernelName = UUID.randomUUID().toString();

    /**
     * Ensure that we have a name suitable for an mbean.
     *
     * @param name the name for the kernel
     * @return a proper mbean name based on the given name
     */
    public static String checkName(String name) {
        String mbeanName = name;
        if (name == null || "".equals(name)) {
            mbeanName = MDSKernel.MBEAN_PREFIX + defaultKernelName + MDSKernel.MBEAN_SUFFIX;
        } else {
            if (!name.startsWith(MDSKernel.MBEAN_PREFIX)) {
                mbeanName = MDSKernel.MBEAN_PREFIX + name + MDSKernel.MBEAN_SUFFIX;
            }
        }
        return mbeanName;
    }

    /**
     * Register a new kernel MBean with the given name or fail
     *
     * @param mBeanName the bean name to use
     * @param kernel    the kernel bean to register
     * @throws IllegalStateException if the MBean cannot be registered
     */
    public static void registerMBean(String mBeanName, MDSKernel kernel) {
        String checkedMBeanName = MDSKernelManager.checkName(mBeanName);
        synchronized (mBeanName) {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            try {
                ObjectName name = new ObjectName(checkedMBeanName);
                if (!mbs.isRegistered(name)) {
                    // register the MBean
                    mbs.registerMBean(kernel, name);
                    log.info("Registered new Kernel MBEAN: " + checkedMBeanName + " [" + kernel + "]");
                }
            } catch (MalformedObjectNameException e) {
                throw new IllegalStateException(e);
            } catch (InstanceAlreadyExistsException e) {
                throw new IllegalStateException(e);
            } catch (MBeanRegistrationException e) {
                throw new IllegalStateException(e);
            } catch (NotCompliantMBeanException e) {
                throw new IllegalStateException(e);
            } catch (NullPointerException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Unregister an MBean if possible
     *
     * @param mBeanName the bean name to use
     * @return true if the MBean was unregistered, false otherwise
     */
    public static boolean unregisterMBean(String mBeanName) {
        String checkedMBeanName = MDSKernelManager.checkName(mBeanName);
        synchronized (mBeanName) {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            try {
                mbs.unregisterMBean(new ObjectName(checkedMBeanName));
                return true;
            } catch (InstanceNotFoundException ie) {
                //If this exception is thrown, the specified MBean is not currently registered
                //So, we'll ignore the error and return true
                return true;
            } catch (Exception e) {
                //log this issue as a System Warning. Also log the underlying error message.
                log.warn("Failed to unregister the MBean: " + checkedMBeanName, e);
                return false;
            }
        }
    }
}
