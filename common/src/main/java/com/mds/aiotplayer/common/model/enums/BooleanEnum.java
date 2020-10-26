/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.model.enums;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 13-2-7 上午11:44
 * <p>Version: 1.0
 */
public enum BooleanEnum {
    TRUE(Boolean.TRUE, "Yes"), FALSE(Boolean.FALSE, "No");

    private final Boolean value;
    private final String info;

    private BooleanEnum(Boolean value, String info) {
        this.value = value;
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public Boolean getValue() {
        return value;
    }
}
