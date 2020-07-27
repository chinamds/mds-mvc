/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://github.com/chinamds/license/
 */
package com.mds.utils.servlet;

import java.io.IOException;
import javax.annotation.Priority;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.mds.kernel.MDSKernel;
import com.mds.kernel.MDSKernelManager;
import com.mds.services.RequestService;


/**
 * This servlet filter will handle the hookup and setup for MDS
 * requests.  It should be applied to any webapp that is using the
 * MDS core.
 * <p>
 * It will also do anything necessary to the requests that are coming
 * into a MDS web application and the responses on their way out.
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
@Priority(1)
public final class MDSWebappServletFilter implements Filter {

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        // ensure the kernel is running, if not then we have to die here
        try {
            getKernel();
        } catch (IllegalStateException e) {
            // no kernel so we die
            String message = "Could not start up MDSWebappServletFilter because the MDS Kernel is unavailable " +
                "or not running: " + e
                .getMessage();
            System.err.println(message);
            throw new ServletException(message, e);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        // clean up the logger for this webapp
        // No longer using commons-logging (JCL), use slf4j instead
        //LogFactory.release(Thread.currentThread().getContextClassLoader());
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet
     * .FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        // now do some MDS stuff
        //try {
        MDSKernel kernel = getKernel();

        // establish the request service startup
        RequestService requestService = kernel.getServiceManager()
                                              .getServiceByName(RequestService.class.getName(), RequestService.class);
        if (requestService == null) {
            throw new IllegalStateException("Could not get the MDS RequestService to start the request transaction");
        }

        // establish a request related to the current session
        requestService.startRequest(request, response); // will trigger the various request listeners
        try {
            // invoke the next filter
            chain.doFilter(request, response);

            // ensure we close out the request (happy request)
            requestService.endRequest(null);
        } catch (Exception e) {
            // failure occurred in the request so we destroy it
            requestService.endRequest(e);
            throw new ServletException(e); // rethrow the exception
        }
            /*
        } catch (Exception e) {
            String message = "Failure in the MDSWebappServletFilter: " + e.getMessage();
            System.err.println(message);
            if (res != null) {
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
            } else {
                throw new ServletException(message, e);
            }*/
        //}
    }

    /**
     * @return the current MDS kernel or fail
     */
    public MDSKernel getKernel() {
        MDSKernel kernel = new MDSKernelManager().getKernel();
        if (!kernel.isRunning()) {
            throw new IllegalStateException("The MDS kernel is not running: " + kernel);
        }
        return kernel;
    }

}
