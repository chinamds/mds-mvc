package com.mds.aiotplayer.sys.model;

import org.hibernate.search.bridge.builtin.StringBridge;

/**
 * Message Folder Bridge
 * <p>User: John Lee
 * <p>Date: 03/08/2020 21:25:37
 * <p>Version: 1.0
 */
public class MessageFolderBridge extends StringBridge {

	@Override
	public String objectToString(Object object) {
		return object.toString();
    }
	
	@Override
	public Object stringToObject(String stringValue) {
        return MessageFolder.valueOf(stringValue);
    }
}