/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.listener;

import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.mds.aiotplayer.cm.util.GalleryUtils;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.common.service.GenericManager;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

/**
 * Class to initialize / cleanup resources used by MDS when the web application
 * is started or stopped.
 */
public class MDSContextListener implements ServletContextListener {
    private static Logger log = LoggerFactory.getLogger(MDSContextListener.class);

    /**
     * Initialize any resources required by the application.
     *
     * @param event This is the event class for notifications about changes to the servlet context of a web application.
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        // On Windows, URL caches can cause problems, particularly with undeployment
        // So, here we attempt to disable them if we detect that we are running on Windows
        try {
            String osName = System.getProperty("os.name");

            if (osName != null && osName.toLowerCase().contains("windows")) {
                URL url = new URL("http://localhost/");
                URLConnection urlConn = url.openConnection();
                urlConn.setDefaultUseCaches(false);
            }
        } catch (RuntimeException e) {
            // Any errors thrown in disabling the caches aren't significant to
            // the normal execution of the application, so we ignore them
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        
        ServletContext context = event.getServletContext();

        // Orion starts Servlets before Listeners, so check if the config
        // object already exists
        Map<String, Object> config = (HashMap<String, Object>) context.getAttribute(Constants.CONFIG);

        if (config == null) {
            config = new HashMap<>();
        }
        
        context.setAttribute(Constants.CONFIG, config);

        setupContext(context);
        
        // Determine version number for CSS and JS Assets
        String appVersion = null;
        try {
            InputStream is = context.getResourceAsStream("/META-INF/MANIFEST.MF");
            if (is == null) {
                log.warn("META-INF/MANIFEST.MF not found.");
            } else {
                Manifest mf = new Manifest();
                mf.read(is);
                Attributes atts = mf.getMainAttributes();
                appVersion = atts.getValue("Implementation-Version");
            }
        } catch (IOException e) {
            log.error("I/O Exception reading manifest: " + e.getMessage());
        }

        // If there was a build number defined in the war, then use it for
        // the cache buster. Otherwise, assume we are in development mode
        // and use a random cache buster so developers don't have to clear
        // their browser cache.
        if (appVersion == null || appVersion.contains("SNAPSHOT")) {
            appVersion = "" + new Random().nextInt(100000);
        }

        log.info("Application version set to: " + appVersion);
        context.setAttribute(Constants.ASSETS_VERSION, appVersion);
        
        try {
			GalleryUtils.initializeMDSApplication(event.getServletContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /**
     * This method uses the LookupManager to lookup available roles from the data layer.
     *
     * @param context The servlet context
     */
    public static void setupContext(ServletContext context) {
        ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(context);

        // Any manager extending GenericManager will do:
        GenericManager manager = (GenericManager) ctx.getBean("userManager");
        doReindexing(manager);
        log.debug("Full text search reindexing complete [OK]");
    }

    private static void doReindexing(GenericManager manager) {
        manager.reindexAll(false);
    }

    /**
     * Clean up resources used by the application when stopped
     *
     * @param event 8     Event class for notifications about changes to the servlet context of a web application.
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        try {
            // Clean out the introspector
            Introspector.flushCaches();

            // Remove any drivers registered by this classloader
            for (Enumeration e = DriverManager.getDrivers(); e.hasMoreElements(); ) {
                Driver driver = (Driver) e.nextElement();
                if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
                    DriverManager.deregisterDriver(driver);
                }
            }
            
            AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (RuntimeException e) {
            log.error("Failed to cleanup ClassLoader for webapp", e);
        } catch (Exception e) {
            log.error("Failed to cleanup ClassLoader for webapp", e);
        }
    }
}
