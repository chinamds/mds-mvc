package com.mds.aiotplayer.sys.model;

/**
 * Module Type
 * <p>User: John Lee
 * <p>Date: 02 December 2017 21:25:37
 * <p>Version: 1.0
 */
public enum ModuleType {

    /**
     * option
     */
    opt("moduletype.option"),
    /**
     * fixed
     */
    fix("moduletype.fixed"),
    /**
     * system
     */
    sys("moduletype.system"),
    /**
     * server
     */
    svr("moduletype.server"),
	/**
     * client
     */
    cli("moduletype.client");

    private final String info;

    private ModuleType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
