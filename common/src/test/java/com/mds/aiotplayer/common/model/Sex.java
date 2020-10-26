/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.model;

/**
 * <p>性别</p>
 * <p>User: Zhang Kaitao
 * <p>Date: 13-1-14 下午2:12
 * <p>Version: 1.0
 */
public enum Sex {

    male("男"), female("女");
    private final String info;

    private Sex(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
