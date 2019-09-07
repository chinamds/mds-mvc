package com.mds.common.webapp.taglib.security;

import org.apache.commons.lang3.ArrayUtils;

import com.mds.sys.util.UserAccount;
import com.mds.sys.util.UserUtils;

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
