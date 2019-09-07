package com.mds.common.webapp.taglib.security;

import com.mds.sys.model.User;
import com.mds.sys.util.RoleUtils;
import com.mds.sys.util.UserAccount;
import com.mds.sys.util.UserUtils;

/**
 * Shiro HasRoleType Tag.
 * 
 * @author John Lee
 */
public class HasRoleTypeTag extends RoleTag  {

	private static final long serialVersionUID = 1L;

	@Override
	protected boolean showTagBody(String roleType) {
        UserAccount user = UserUtils.getUser();
        if (user != null) {
			return user.getRoles().stream().anyMatch(r->r.getRoleType().toString().equals(roleType));
        }
        
        return false;
	}

}
