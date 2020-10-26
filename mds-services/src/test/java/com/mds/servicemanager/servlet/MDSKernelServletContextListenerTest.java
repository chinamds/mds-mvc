/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.servicemanager.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import com.mds.kernel.MDSKernelManager;
import com.mds.utils.servlet.MDSWebappServletFilter;
import org.junit.Test;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;


/**
 * This starts up a jetty server and tests the ability for the servlet filter to start a kernel
 * and correctly shut it down
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class MDSKernelServletContextListenerTest {

    @Test
    public void testSampleRequest() {
        // make sure no kernel yet
        try {
            new MDSKernelManager().getKernel();
            fail("Should have thrown exception");
        } catch (IllegalStateException e) {
            assertNotNull(e.getMessage());
        }

        ServletTester tester = new ServletTester();
        tester.setContextPath("/");
        tester.getContext().addEventListener(new MDSKernelServletContextListener());
        tester.addFilter(MDSWebappServletFilter.class, "/*", Handler.REQUEST);
        tester.addServlet(SampleServlet.class, "/mds");
        try {
            tester.start();
        } catch (Exception e) {
            fail("Could not start the jetty server: " + e.getMessage());
        }

        // now there should be a kernel
        assertNotNull(new MDSKernelManager().getKernel());

        // now fire the request
        String jettyRequest =
            "GET /mds HTTP/1.1\r\n" +
                "Host: tester\r\n" +
                "\r\n";
        try {
            String content = tester.getResponses(jettyRequest);
            assertNotNull(content);
            assertTrue(content.contains("MDSTest"));
//            assertFalse(content.contains("session=null"));
//            assertFalse(content.contains("request=null"));
        } catch (Exception e) {
            fail("Could not fire request: " + e.getMessage());
        }

        // now there should be a kernel
        assertNotNull(new MDSKernelManager().getKernel());

        // try a request a different way
        HttpTester request = new HttpTester();
        HttpTester response = new HttpTester();
        request.setMethod("GET");
        request.setHeader("Host", "tester");
        request.setVersion("HTTP/1.0");
        request.setURI("/mds");

        try {
            response.parse(tester.getResponses(request.generate()));
        } catch (IOException e1) {
            fail("Could not parse response: " + e1.getMessage());
        } catch (Exception e1) {
            fail("Could not parse response: " + e1.getMessage());
        }

        assertTrue(response.getMethod() == null);
        assertEquals(200, response.getStatus());
        String content = response.getContent();
        assertNotNull(content);
        assertTrue(content.contains("MDSTest"));
//        assertFalse(content.contains("session=null"));
//        assertFalse(content.contains("request=null"));

        // now there should be a kernel
        assertNotNull(new MDSKernelManager().getKernel());

        try {
            tester.stop();
        } catch (Exception e) {
            fail("Could not stop the jetty server: " + e.getMessage());
        }

        // back to no kernel again
        try {
            new MDSKernelManager().getKernel();
            fail("Should have thrown exception");
        } catch (IllegalStateException e) {
            assertNotNull(e.getMessage());
        }

        tester = null;
        request = null;
        response = null;
    }

    @Test
    public void testSampleRequestDouble() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        testSampleRequest();
    }


}
