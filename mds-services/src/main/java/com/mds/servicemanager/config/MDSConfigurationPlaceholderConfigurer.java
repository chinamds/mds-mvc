/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.servicemanager.config;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.spring.ConfigurationPropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.MutablePropertySources;

/**
 * Extends Spring PropertySourcesPlaceholderConfigurer to allow our Configuration to be included as a Spring
 * PropertySource. This allows ${...} placeholders within bean definition property values and @Value annotations
 * to be resolved using MDSConfigurationService
 * <P>
 * See: https://stackoverflow.com/a/36718301/3750035
 * <P>
 * NOTE: This is initialized in spring-mds-core-services.xml
 *
 * @see PropertySourcesPlaceholderConfigurer
 * @see MDSConfigurationService
 */
public class MDSConfigurationPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {

    public MDSConfigurationPlaceholderConfigurer(Configuration configuration) {
        ConfigurationPropertySource apacheCommonsConfigPropertySource =
            new ConfigurationPropertySource(configuration.getClass().getName(), configuration);
        MutablePropertySources propertySources = new MutablePropertySources();
        propertySources.addLast(apacheCommonsConfigPropertySource);
        setPropertySources(propertySources);
    }
}
