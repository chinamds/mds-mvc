/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.i18n.model;

/**
 * Resource Category
 * <p>User: John Lee
 * <p>Date: 02 December 2017 21:25:37
 * <p>Version: 1.0
 */
public enum ResourceCategory {

	/// <summary>
	/// No Resource category has been specified.
	/// </summary>
	notspecified("NotSpecified"),
    /**
     * menu
     */
    menu("ResourceCategory.menu"),
    /**
     * errors
     */
    errors("ResourceCategory.errors"),
    /**
     * button
     */
    button("ResourceCategory.button"),
    /**
     * import
     */
    message("ResourceCategory.message"),
    /**
     * date
     */
    date("ResourceCategory.date"),
    /**
     * area
     */
    area("ResourceCategory.area"),
    /**
     * user
     */
    user("ResourceCategory.user"),
    /**
     * area
     */
    cms_theme("ResourceCategory.cms_theme"),
	/**
     * login
     */
    login("ResourceCategory.login"),
    /**
     * treeselect
     */
    treeselect("ResourceCategory.treeselect"),
    /**
     * others
     */
    webapp("ResourceCategory.webapp");

    private final String info;

    private ResourceCategory(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
