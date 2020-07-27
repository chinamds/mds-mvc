/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.util;

import java.io.File;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.commons.lang3.StringUtils;
import com.mds.kernel.MDSKernel;
import com.mds.kernel.MDSKernelManager;
import com.mds.servicemanager.MDSKernelImpl;
import com.mds.servicemanager.MDSKernelInit;
import com.mds.servicemanager.config.MDSConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Utility class that will initialize the MDS Kernel on Spring Boot startup.
 * Used by com.mds.aiotplayer.app.rest.Application
 */
public class MDSKernelInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger log = LoggerFactory.getLogger(MDSKernelInitializer.class);

    private transient MDSKernel mdsKernel;

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        // Check if the kernel is already started
        this.mdsKernel = MDSKernelManager.getDefaultKernel();
        if (this.mdsKernel == null) {
            MDSKernelImpl kernelImpl = null;
            try {
                // Load the kernel with default settings
                kernelImpl = MDSKernelInit.getKernel(null);
                if (!kernelImpl.isRunning()) {
                    // Determine configured MDS home & init the Kernel
                    kernelImpl.start(getMDSHome(applicationContext.getEnvironment()));
                }
                this.mdsKernel = kernelImpl;

            } catch (Exception e) {
                // failed to start so destroy it and log and throw an exception
                try {
                    if (kernelImpl != null) {
                        kernelImpl.destroy();
                    }
                    this.mdsKernel = null;
                } catch (Exception e1) {
                    // nothing
                }
                String message = "Failure during ServletContext initialisation: " + e.getMessage();
                log.error(message, e);
                throw new RuntimeException(message, e);
            }
        }

        if (applicationContext.getParent() == null) {
            // Set the MDS Kernel Application context as a parent of the Spring Boot context so that
            // we can auto-wire all MDS Kernel services
            applicationContext.setParent(mdsKernel.getServiceManager().getApplicationContext());

            //Add a listener for Spring Boot application shutdown so that we can nicely cleanup the MDS kernel.
            applicationContext.addApplicationListener(new MDSKernelDestroyer(mdsKernel));
        }
    }

    /**
     * Find MDS's "home" directory (from current environment)
     * Initially look for JNDI Resource called "java:/comp/env/mds.home".
     * If not found, use value provided in "mds.home" in Spring Environment
     */
    private String getMDSHome(ConfigurableEnvironment environment) {
        // Load the "mds.home" property from Spring Boot's Configuration (application.properties)
        // This gives us the location of our MDS configurations, necessary to start the kernel
        String providedHome = environment.getProperty(MDSConfigurationService.MDS_HOME);

        String mdsHome = null;
        try {
            // Allow ability to override home directory via JNDI
            Context ctx = new InitialContext();
            mdsHome = (String) ctx.lookup("java:/comp/env/" + MDSConfigurationService.MDS_HOME);
        } catch (Exception e) {
            // do nothing
        }

        // Otherwise, verify the 'providedHome' value is non-empty, exists and includes MDS configs
        if (mdsHome == null) {
            if (StringUtils.isNotBlank(providedHome) &&
                !providedHome.equals("${" + MDSConfigurationService.MDS_HOME + "}")) {
                File test = new File(providedHome);
                if (test.exists() && new File(test, MDSConfigurationService.MDS_CONFIG_PATH).exists()) {
                    mdsHome = providedHome;
                }
            }
        }
        return mdsHome;
    }


    /**
     * Utility class that will destroy the MDS Kernel on Spring Boot shutdown
     */
    private class MDSKernelDestroyer implements ApplicationListener<ContextClosedEvent> {
        private MDSKernel kernel;

        public MDSKernelDestroyer(MDSKernel kernel) {
            this.kernel = kernel;
        }

        public void onApplicationEvent(final ContextClosedEvent event) {
            if (this.kernel != null) {
                this.kernel.destroy();
                this.kernel = null;
            }
        }
    }
}

