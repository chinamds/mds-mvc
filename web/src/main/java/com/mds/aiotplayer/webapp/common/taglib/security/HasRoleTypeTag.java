package com.mds.aiotplayer.webapp.common.taglib.security;

import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;

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
