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
 * Shiro HasAnyPermissions Tag.
 * 
 * @author calvin
 */
public class HasAnyPermissionsTag extends PermissionTag {

	private static final long serialVersionUID = 1L;
	private static final String PERMISSION_NAMES_DELIMETER = ",";

	@Override
	protected boolean showTagBody(String permissionNames) {
		boolean hasAnyPermission = false;

		Authentication subject = getSubject();

		if (subject != null && subject.getPrincipal() instanceof UserAccount) {		
			// Iterate through permissions and check to see if the user has one of the permissions
			for (String permission : permissionNames.split(PERMISSION_NAMES_DELIMETER)) {
				if (((UserAccount)getSubject().getPrincipal()).isPermitted(permission.trim())) {
					hasAnyPermission = true;
					break;
				}

			}
		}

		return hasAnyPermission;
	}

}
