/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://github.com/chinamds/license/
 */
package com.mds.services.session;

import com.mds.services.model.RequestInterceptor;


/**
 * This is a mock request interceptor for testing
 *
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class MockRequestInterceptor implements RequestInterceptor {

    public String state = "";
    public int hits = 0;

    /* (non-Javadoc)
     * @see com.mds.services.model.RequestInterceptor#onEnd(java.lang.String, com.mds.services.model.Session,
     * boolean, java.lang.Exception)
     */
    public void onEnd(String requestId, boolean succeeded, Exception failure) {
        if (succeeded) {
            state = "end:success:" + requestId;
        } else {
            state = "end:fail:" + requestId;
        }
        hits++;
    }

    /* (non-Javadoc)
     * @see com.mds.services.model.RequestInterceptor#onStart(java.lang.String, com.mds.services.model.Session)
     */
    public void onStart(String requestId) {
        state = "start:" + requestId;
        hits++;
    }

    /* (non-Javadoc)
     * @see com.mds.kernel.mixins.OrderedService#getOrder()
     */
    public int getOrder() {
        return 10;
    }

}
