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
