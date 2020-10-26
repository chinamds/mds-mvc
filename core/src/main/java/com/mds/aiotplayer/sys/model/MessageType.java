/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.model;

/**
 * message type
 * <p>User: Zhang Kaitao
 * <p>Date: 13-5-22 下午1:59
 * <p>Version: 1.0
 */
public enum MessageType {
    usr("user message"),
    sys("system message");

    private final String info;

    private MessageType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
