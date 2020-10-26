/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.taglib.security;

import com.mds.aiotplayer.sys.util.UserAccount;

/**
 * @since 0.1
 */
public class LacksRoleTag extends RoleTag {

    //TODO - complete JavaDoc

    public LacksRoleTag() {
    }

    protected boolean showTagBody(String roleName) {
        boolean hasRole = false;
        if (getSubject() != null && getSubject().getPrincipal() instanceof UserAccount)
        	hasRole =   ((UserAccount)getSubject().getPrincipal()).hasRole(roleName);
        
        return !hasRole;
    }

}
