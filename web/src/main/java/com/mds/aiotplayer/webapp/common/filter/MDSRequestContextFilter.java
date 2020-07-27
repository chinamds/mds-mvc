/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/*import com.mds.aiotplayer.core.Context;
import com.mds.aiotplayer.app.rest.util.ContextUtil;*/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Servlet Filter whose only role is to clean up open Context objects in
 * the request. (These Context objects may have been created by Controllers
 * in order to populate Views).
 *
 * @author Tim Donohue
 * @see ContextUtil
 */
public class MDSRequestContextFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(MDSRequestContextFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //noop
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        //Context context = null;
        // First, process any other servlet filters, along with the controller & view
        chain.doFilter(request, response);

        // *After* view was processed, check for an open Context object in the ServletRequest
        // (This Context object may have been opened by a @Controller via ContextUtil.obtainContext())
        //context = (Context) request.getAttribute(ContextUtil.MDS_CONTEXT);
    }

    @Override
    public void destroy() {
        //noop
    }
}