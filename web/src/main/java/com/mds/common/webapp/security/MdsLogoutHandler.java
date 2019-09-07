package com.mds.common.webapp.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public class MdsLogoutHandler implements LogoutHandler {

	public MdsLogoutHandler() {
	}

	@Override
	public void logout(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication) {
		System.out.println("MdsLogoutSuccessHandler.logout() is called!");

	}

}
