/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.services.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.mds.services.CachingService;
import com.mds.services.model.Cache;
import com.mds.services.model.CacheConfig;
import com.mds.services.model.CacheConfig.CacheScope;
import com.mds.services.sessions.StatelessRequestServiceImpl;
import com.mds.test.MDSAbstractKernelTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Testing the request and session services
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class StatelessRequestServiceImplTest extends MDSAbstractKernelTest {

    private StatelessRequestServiceImpl statelessRequestService;
    private CachingService cachingService;

    @Before
    public void before() {
        statelessRequestService = getService(StatelessRequestServiceImpl.class);
        cachingService = getService(CachingService.class);
    }

    @After
    public void after() {
        statelessRequestService.clear();
        cachingService.resetCaches();
        statelessRequestService = null;
        cachingService = null;
    }

    /**
     * Test method for {@link com.mds.services.sessions.StatelessRequestServiceImpl#startRequest()}.
     */
    @Test
    public void testStartRequest() {
        String requestId = statelessRequestService.startRequest();
        assertNotNull(requestId);

        statelessRequestService.endRequest(null);
    }

    /**
     * Test method for {@link com.mds.services.sessions.StatelessRequestServiceImpl#endRequest(java.lang.Exception)}.
     */
    @Test
    public void testEndRequest() {
        String requestId = statelessRequestService.startRequest();
        assertNotNull(requestId);

        statelessRequestService.endRequest(null);
        assertNull(getRequestCache());
    }

    /**
     * Test method for
     * {@link com.mds.services.sessions.StatelessRequestServiceImpl#registerRequestInterceptor(com.mds.services.model.RequestInterceptor)}.
     */
    @Test
    public void testRegisterRequestListener() {
        MockRequestInterceptor mri = new MockRequestInterceptor();
        statelessRequestService.registerRequestInterceptor(mri);
        assertEquals("", mri.state);
        assertEquals(0, mri.hits);

        String requestId = statelessRequestService.startRequest();
        assertEquals(1, mri.hits);
        assertTrue(mri.state.startsWith("start"));
        assertTrue(mri.state.contains(requestId));

        statelessRequestService.endRequest(null);
        assertEquals(2, mri.hits);
        assertTrue(mri.state.startsWith("end"));
        assertTrue(mri.state.contains("success"));
        assertTrue(mri.state.contains(requestId));

        requestId = statelessRequestService.startRequest();
        assertEquals(3, mri.hits);
        assertTrue(mri.state.startsWith("start"));
        assertTrue(mri.state.contains(requestId));

        statelessRequestService.endRequest(new RuntimeException("Oh Noes!"));
        assertEquals(4, mri.hits);
        assertTrue(mri.state.startsWith("end"));
        assertTrue(mri.state.contains("fail"));
        assertTrue(mri.state.contains(requestId));

        try {
            statelessRequestService.registerRequestInterceptor(null);
            fail("should have thrown exception");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
    }

    /**
     * Test method for {@link com.mds.services.sessions.StatelessRequestServiceImpl#getCurrentUserId()}.
     */
    @Test
    public void testGetCurrentUserId() {
        String current = statelessRequestService.getCurrentUserId();
        assertNull(current);
    }

    /**
     * Test method for {@link com.mds.services.sessions.StatelessRequestServiceImpl#getCurrentRequestId()}.
     */
    @Test
    public void testGetCurrentRequestId() {
        String requestId = statelessRequestService.getCurrentRequestId();
        assertNull(requestId); // no request yet

        String rid = statelessRequestService.startRequest();

        requestId = statelessRequestService.getCurrentRequestId();
        assertNotNull(requestId);
        assertEquals(rid, requestId);

        statelessRequestService.endRequest(null);

        requestId = statelessRequestService.getCurrentRequestId();
        assertNull(requestId); // no request yet
    }


    /**
     * @return the request storage cache
     */
    private Cache getRequestCache() {
        return cachingService.getCache(CachingService.REQUEST_CACHE, new CacheConfig(CacheScope.REQUEST));
    }

}
