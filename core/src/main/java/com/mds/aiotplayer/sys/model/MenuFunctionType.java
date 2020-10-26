/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.model;

/**
 * MenuFunction Type
 * <p>User: John Lee
 * <p>Date: 02 December 2017 21:25:37
 * <p>Version: 1.0
 */
public enum MenuFunctionType {

    /**
     * menu
     */
    m("MenuFunctionType.m"),
    /**
     * function
     */
    f("MenuFunctionType.f"),
    /**
     * others
     */
    o("MenuFunctionType.o");

    private final String info;

    private MenuFunctionType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
