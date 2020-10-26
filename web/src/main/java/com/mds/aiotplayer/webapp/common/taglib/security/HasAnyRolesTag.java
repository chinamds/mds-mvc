/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.taglib.security;

import org.springframework.security.core.Authentication;

import com.mds.aiotplayer.sys.util.UserAccount;

/**
 * Displays body content if the current user has any of the roles specified.
 *
 * @since 0.2
 */
public class HasAnyRolesTag extends RoleTag {

    //TODO - complete JavaDoc

    // Delimeter that separates role names in tag attribute
    private static final String ROLE_NAMES_DELIMETER = ",";

    public HasAnyRolesTag() {
    }

    protected boolean showTagBody(String roleNames) {
        boolean hasAnyRole = false;

        Authentication subject = getSubject();

        if (subject != null && subject.getPrincipal() instanceof UserAccount) {	

            // Iterate through roles and check to see if the user has one of the roles
            for (String role : roleNames.split(ROLE_NAMES_DELIMETER)) {

                if (((UserAccount)getSubject().getPrincipal()).hasRole(role.trim())) {
                    hasAnyRole = true;
                    break;
                }

            }

        }

        return hasAnyRole;
    }

}
