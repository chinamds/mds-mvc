/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://github.com/chinamds/license/
 */
package com.mds.services.events;

import com.mds.services.model.EventListener;

/**
 * This is a sample event listener for testing,
 * it does filtering on the name only
 *
 * @author Aaron Zeckoski (azeckoski@gmail.com) - azeckoski - 1:17:31 PM Nov 20, 2008
 */
public class EventListenerNameFilter extends EventListenerNoFilter implements EventListener {

    /* (non-Javadoc)
     * @see com.mds.services.model.EventListener#getEventNamePrefixes()
     */
    @Override
    public String[] getEventNamePrefixes() {
        // only receive events which start with aaron or test
        return new String[] {"aaron", "test"};
    }

}
