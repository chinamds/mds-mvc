package com.mds.common.webapp.security;

import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

public class MdsLogoutFilter extends LogoutFilter {

	public MdsLogoutFilter(String logoutSuccessUrl, LogoutHandler[] handlers) {
		super(logoutSuccessUrl, handlers);
	}

	public MdsLogoutFilter(LogoutSuccessHandler logoutSuccessHandler,
			LogoutHandler[] handlers) {
		super(logoutSuccessHandler, handlers);
	}

}
