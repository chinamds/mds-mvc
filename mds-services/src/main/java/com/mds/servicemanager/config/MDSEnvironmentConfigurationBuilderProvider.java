/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.servicemanager.config;

import java.util.Collections;

import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import org.apache.commons.configuration2.builder.combined.BaseConfigurationBuilderProvider;

/**
 * Configures MDSEnvironmentConfiguration. Reuses BasicConfigurationBuilder and its parameters.
 *
 * @author Pascal-Nicolas Becker -- mds at pascal dash becker dot de
 */
public class MDSEnvironmentConfigurationBuilderProvider extends BaseConfigurationBuilderProvider {

    /**
     * Creates a new instance of {@code BaseConfigurationBuilderProvider} and
     * initializes all its properties.
     */
    public MDSEnvironmentConfigurationBuilderProvider() {
        super("org.apache.commons.configuration2.builder.BasicConfigurationBuilder",
                null,
                "com.mds.servicemanager.config.MDSEnvironmentConfiguration",
                // this probably contains much more than we need, nevertheless reusing it is easier than rewriting
                Collections.singleton(BasicBuilderParameters.class.getName()));
    }
}
