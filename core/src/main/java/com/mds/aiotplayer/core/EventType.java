/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

/// <summary>
/// Specifies the type of event that can occur in the gallery.
/// </summary>
public enum EventType{
	NotSpecified(0),
	Info(1),
	Warning(2),
	Error(3);

	private final int eventType;
    
    private EventType(int eventType) {
        this.eventType = eventType;
    }
    
    public int value() {
    	return eventType;
    }
}