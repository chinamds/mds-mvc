package com.mds.aiotplayer.webapp.configuration;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;

import org.springframework.boot.web.servlet.ServletContextInitializer;


/**
 * <p>
 * equivalent to  "session-config" or "cookie-config" tag on WEB-INF/web.xml . 
 * </p>
 */
public class SessionTrackingConfigListener implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext)
            throws ServletException {
        SessionCookieConfig sessionCookieConfig = servletContext
                .getSessionCookieConfig();
        sessionCookieConfig.setHttpOnly(true);
        sessionCookieConfig.setName("SAMPLESESSIONID");
        Set<SessionTrackingMode> stmSet = new HashSet<SessionTrackingMode>();
        stmSet.add(SessionTrackingMode.COOKIE);
        servletContext.setSessionTrackingModes(stmSet);
    }
}
