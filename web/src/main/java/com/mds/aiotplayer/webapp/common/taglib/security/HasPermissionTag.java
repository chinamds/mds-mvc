/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.taglib.security;

/**
 * @since 0.1
 */
public class HasPermissionTag extends PermissionTag {

    //TODO - complete JavaDoc

    public HasPermissionTag() {
    }

    protected boolean showTagBody(String p) {
        return isPermitted(p);
    }

}
