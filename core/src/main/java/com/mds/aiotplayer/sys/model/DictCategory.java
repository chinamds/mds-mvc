/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.model;

/**
 * dictionary  category
 * <p>User: John Lee
 * <p>Date: 02 December 2017 21:25:37
 * <p>Version: 1.0
 */
public enum DictCategory {

	/// <summary>
	/// No dictionary category has been specified.
	/// </summary>
	notspecified("NotSpecified"),
    /**
     * Role Type
     */
    roletype("RoleType"),
    /**
     * Company Administrators
     */
    mds_theme("Theme"),
    /**
     * company users
     */
    i18n("i18n"),
    /**
     * company guests
     */
    cg("companyguest"),
    /**
     * users
     */
    ur("user"),
	/**
     * guests
     */
    gt("guest");

    private final String info;

    private DictCategory(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
