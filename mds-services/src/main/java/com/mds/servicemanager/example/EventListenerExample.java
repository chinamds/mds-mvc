/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.servicemanager.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mds.services.model.Event;
import com.mds.services.model.EventListener;

/**
 * A sample EventListener which writes a string form of each received
 * Event to the MDS log.
 *
 * @author Mark Diggory (mdiggory at atmire.com)
 * @version $Revision$
 */
public final class EventListenerExample implements EventListener {

    /**
     * log4j category
     */
	private static Logger log = LoggerFactory.getLogger(EventListenerExample.class);

    /**
     * @return null
     */
    public String[] getEventNamePrefixes() {
        return null;
    }

    /**
     * @return null
     */
    public String getResourcePrefix() {
        return null;
    }

    public void receiveEvent(Event event) {
        log.info(event.toString());
    }

}
