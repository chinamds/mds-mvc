package com.mds.aiotplayer.sys.model;

/**
 * recipient type
 * <p>User: John Lee
 * <p>Date: 2017/8/17 19:57
 * <p>Version: 1.0
 */
public enum RecipientType {
    to("recipient"),
    cc("carbon copy recipient"),
	bcc("blind carbon copy recipient");

    private final String info;

    private RecipientType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
