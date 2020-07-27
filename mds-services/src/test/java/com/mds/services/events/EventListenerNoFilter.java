/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://github.com/chinamds/license/
 */
package com.mds.services.events;

import java.util.List;
import java.util.Vector;

import com.mds.services.model.Event;
import com.mds.services.model.EventListener;

/**
 * This is a sample event listener for testing,
 * it does no filtering
 *
 * @author Aaron Zeckoski (azeckoski@gmail.com) - azeckoski - 1:17:31 PM Nov 20, 2008
 */
public class EventListenerNoFilter implements EventListener {

    public List<Event> received = new Vector<Event>();

    public List<Event> getReceivedEvents() {
        return received;
    }

    public void clearReceivedEvents() {
        received.clear();
    }

    /* (non-Javadoc)
     * @see com.mds.services.model.EventListener#getEventNamePrefixes()
     */
    public String[] getEventNamePrefixes() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.mds.services.model.EventListener#getResourcePrefix()
     */
    public String getResourcePrefix() {
        return null;
    }

    /* (non-Javadoc)
     * @see com.mds.services.model.EventListener#receiveEvent(com.mds.services.model.Event)
     */
    public void receiveEvent(Event event) {
        received.add(event);
    }

}
