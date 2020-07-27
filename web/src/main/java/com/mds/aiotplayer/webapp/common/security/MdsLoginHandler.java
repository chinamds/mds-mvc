package com.mds.aiotplayer.webapp.common.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.mds.aiotplayer.sys.util.UserUtils;

public class MdsLoginHandler extends
		SavedRequestAwareAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws ServletException, IOException {
        if (!UserUtils.isSysUserLogin()) {
            // then we redirect
            getRedirectStrategy().sendRedirect(request, response, "/");
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
        
		/*super.onAuthenticationSuccess(request, response, authentication);

		System.out
				.println("MdsLoginHandler.onAuthenticationSuccess() is called!");*/
	}

}
