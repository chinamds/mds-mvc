package com.mds.aiotplayer.sys.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Menu Target
 * <p>User: John Lee
 * <p>Date: 02 December 2017 21:25:37
 * <p>Version: 1.0
 */
public enum MenuTarget {

	/// <summary>
	/// No target has been specified.
	/// </summary>
	notspecified("menutarget.notspecified"),
    /**
     * new window
     */
    _blank("menutarget._blank"),
    /**
     * current window
     */
    _self("menutarget._self"),
    /**
     * top window
     */
	_top("menutarget._top"),
	/**
     * parent window
     */
    _parent("menutarget._parent");
	/**
     * mainframe window
     *//*
    mainframe("menutarget.mainframe");*/


    private final String info;

    private MenuTarget(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}