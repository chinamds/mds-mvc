/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.filter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Borrowed from the Display Tag project:
 * http://displaytag.sourceforge.net/xref-test/org/displaytag/filter/MockFilterSupport.html
 *
 * Todo: look into using Spring's MockFilterChain:
 * http://www.springframework.org/docs/api/org/springframework/mock/web/MockFilterChain.html
 */
public class MockFilterChain implements FilterChain {
    private final Logger log = LoggerFactory.getLogger(MockFilterChain.class);
    private String forwardURL;

    public void doFilter(ServletRequest request, ServletResponse response)
    throws IOException, ServletException {
        String uri = ((HttpServletRequest) request).getRequestURI();
        String requestContext = ((HttpServletRequest) request).getContextPath();

        if (StringUtils.isNotEmpty(requestContext) && uri.startsWith(requestContext)) {
            uri = uri.substring(requestContext.length());
        }

        this.forwardURL = uri;
        log.debug("Forwarding to: " + uri);

        RequestDispatcher dispatcher = request.getRequestDispatcher(uri);
        dispatcher.forward(request, response);
    }

    public String getForwardURL() {
        return this.forwardURL;
    }
}