/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.AuthenticationDetailsSource;

import com.mds.aiotplayer.sys.util.UserAccount;

public class MdsAuthenticationDetailsSource implements
		AuthenticationDetailsSource<HttpServletRequest, UserAccount> {

	@Override
	public UserAccount buildDetails(HttpServletRequest context) {
		return new UserAccount(context.toString());
	}
}
