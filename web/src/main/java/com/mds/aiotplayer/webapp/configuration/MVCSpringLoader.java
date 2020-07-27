/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.configuration;

import java.io.File;
import java.net.MalformedURLException;

import com.mds.kernel.config.SpringLoader;
import com.mds.services.ConfigurationService;

/**
 * @author Kevin Van de Velde (kevin at atmire dot com)
 */
public class MVCSpringLoader implements SpringLoader {

    @Override
    public String[] getResourcePaths(ConfigurationService configurationService) {
        StringBuffer filePath = new StringBuffer();
        filePath.append(configurationService.getProperty("mdsplus.home"));
        filePath.append(File.separator);
        filePath.append("config");
        filePath.append(File.separator);
        filePath.append("spring");
        filePath.append(File.separator);
        filePath.append("mvc");
        filePath.append(File.separator);


        try {
            return new String[] {new File(filePath.toString()).toURI().toURL().toString() + XML_SUFFIX};
        } catch (MalformedURLException e) {
            return new String[0];
        }
    }
}