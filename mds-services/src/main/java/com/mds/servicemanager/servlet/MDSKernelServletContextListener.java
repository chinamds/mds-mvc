/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.servicemanager.servlet;

import java.io.File;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.mds.servicemanager.MDSKernelImpl;
import com.mds.servicemanager.MDSKernelInit;
import com.mds.servicemanager.config.MDSConfigurationService;


/**
 * This servlet context listener will handle startup of the kernel if it
 * is not there.
 * Shutdown of the context listener does not shutdown the kernel though;
 * that is tied to the shutdown of the JVM.
 * <p>
 * This is implemented in the web application web.xml using:
 * <pre>
 * {@code <listener>}
 * {@code   <listener-class>}
 * {@code     com.mds.servicemanager.servlet.MDSKernelServletContextListener}
 * {@code   </listener-class>}
 * {@code </listener>}
 * </pre>
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 * @author Mark Diggory (mdiggory @ gmail.com)
 * @deprecated The MDS Kernel initialisation is now done by com.mds.app.rest.Application
 */
@Deprecated
public final class MDSKernelServletContextListener implements ServletContextListener {

    private transient MDSKernelImpl kernelImpl;

    /*
     * Find MDS's "home" directory.
     * Initially look for JNDI Resource called "java:/comp/env/mdsplus.home".
     * If not found, look for "mdsplus.home" initial context parameter.
     */
    private String getProvidedHome(ServletContextEvent arg0) {
        String providedHome = null;
        try {
            Context ctx = new InitialContext();
            providedHome = (String) ctx.lookup("java:/comp/env/" + MDSConfigurationService.MDS_HOME);
        } catch (Exception e) {
            // do nothing
        }

        if (providedHome == null) {
            String mdsHome = arg0.getServletContext().getInitParameter(MDSConfigurationService.MDS_HOME);
            if (mdsHome != null && !mdsHome.equals("") &&
                !mdsHome.equals("${" + MDSConfigurationService.MDS_HOME + "}")) {
                File test = new File(mdsHome);
                if (test.exists() && new File(test, MDSConfigurationService.MDS_CONFIG_PATH).exists()) {
                    providedHome = mdsHome;
                }
            }
        }
        return providedHome;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
        // start the kernel when the webapp starts
        try {
            this.kernelImpl = MDSKernelInit.getKernel(null);
            if (!this.kernelImpl.isRunning()) {
                this.kernelImpl.start(getProvidedHome(arg0)); // init the kernel
            }
        } catch (Exception e) {
            // failed to start so destroy it and log and throw an exception
            try {
                this.kernelImpl.destroy();
            } catch (Exception e1) {
                // nothing
            }
            String message = "Failure during filter init: " + e.getMessage();
            System.err.println(message + ":" + e);
            throw new RuntimeException(message, e);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
        // currently we are stopping the kernel when the webapp stops
        if (this.kernelImpl != null) {
            this.kernelImpl.destroy();
            this.kernelImpl = null;
        }
        // No longer going to use JCL
//        // clean up the logger for this webapp
//        LogFactory.release(Thread.currentThread().getContextClassLoader());
        // No longer cleaning this up here since it causes failures
//        // cleanup the datasource
//        try {
//            for (Enumeration<?> e = DriverManager.getDrivers(); e.hasMoreElements(); ) {
//                Driver driver = (Driver) e.nextElement();
//                if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
//                    DriverManager.deregisterDriver(driver);
//                }
//            }
//        } catch (Throwable e) {
//            System.err.println("Unable to clean up JDBC driver: " + e.getMessage());
//        }
    }

}
