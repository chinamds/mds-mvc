/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.util;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.spring.ConfigurationPropertySource;
import com.mds.services.ConfigurationService;
import com.mds.services.factory.MDSServicesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Utility class that will initialize the MDS Configuration on Spring Boot startup.
 * <P>
 * NOTE: MUST be loaded after MDSKernelInitializer, as it requires the kernel is already initialized.
 * <P>
 * This initializer ensures that our MDS Configuration is loaded into Spring's list of PropertySources
 * very early in the Spring Boot startup process. That is important as it allows us to use MDS configurations
 * within @ConditionalOnProperty annotations on beans, as well as @Value annotations and XML bean definitions.
 * <P>
 * Used by com.mds.aiotplayer.app.rest.Application
 */
public class MDSConfigurationInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger log = LoggerFactory.getLogger(MDSConfigurationInitializer.class);

    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {
        // Load MDS Configuration service (requires kernel already initialized)
        ConfigurationService configurationService = MDSServicesFactory.getInstance().getConfigurationService();
        Configuration configuration = configurationService.getConfiguration();

        // Create an Apache Commons Configuration Property Source from our configuration
        ConfigurationPropertySource apacheCommonsConfigPropertySource =
            new ConfigurationPropertySource(configuration.getClass().getName(), configuration);

        // Prepend it to the Environment's list of PropertySources
        // NOTE: This is added *first* in the list so that settings in MDS's ConfigurationService *override*
        // any default values in Spring Boot's application.properties (or similar)
        applicationContext.getEnvironment().getPropertySources().addFirst(apacheCommonsConfigPropertySource);
    }
}

