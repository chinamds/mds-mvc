/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.model;

/**
 * Authorization type
 * <p>User: John Lee
 * <p>Date: 07/09/2017 17:36:34
 * <p>Version: 1.0
 */
public enum AuthType {

    user("User"), user_group("User Group"), department("Department"), company("Company"), area("Area");

    private final String info;

    private AuthType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
