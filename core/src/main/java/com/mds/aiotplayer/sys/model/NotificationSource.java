/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.model;

/**
 * notification trigger by
 * <p>User: Zhang Kaitao
 * <p>Date: 2017/8/18 12:55
 * <p>Version: 1.0
 */
public enum NotificationSource {

    system("system"), excel("excel");

    private final String info;

    private NotificationSource(final String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

}
