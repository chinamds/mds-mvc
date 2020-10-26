/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.taglib.security;

import org.apache.commons.lang3.ArrayUtils;

import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;

/**
 * Shiro HasRoleType Tag.
 * 
 * @author John Lee
 */
public class HasAnyRoleTypesTag extends RoleTag  {

	private static final long serialVersionUID = 1L;
	private static final String ROLE_TYPES_DELIMETER = ",";

	@Override
	protected boolean showTagBody(String roleTypes) {
        UserAccount user = UserUtils.getUser();

        if (user != null) {
        	return user.getRoles().stream().anyMatch(r->ArrayUtils.contains(roleTypes.split(ROLE_TYPES_DELIMETER), r.getRoleType().toString()));
        }
        
        return false;
	}

}
