package com.mds.aiotplayer.webapp.common.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class CaptchaCaptureFilter extends OncePerRequestFilter {

	private Logger logger = LoggerFactory.getLogger(CaptchaCaptureFilter.class);
	private String userCaptchaResponse;
	private HttpServletRequest request;
	
	private boolean jcaptchaEnabled = true;
	
	public CaptchaCaptureFilter() {
	}
	
	public CaptchaCaptureFilter(boolean jcaptchaEnabled) {
		this();
		
		this.jcaptchaEnabled = jcaptchaEnabled;
	}
	
	@Override
	public void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
								 FilterChain chain) throws IOException, ServletException {

		logger.debug("Captcha capture filter");
		if (req.getParameter("jcaptchaCode") == null) {
			req.setAttribute("jcaptchaEnabled", jcaptchaEnabled);
		}

		// Assign values only when user has submitted a Captcha value.
		// Without this condition the values will be reset due to redirection
		// and CaptchaVerifierFilter will enter an infinite loop
		request = req;
		if (jcaptchaEnabled && req.getParameter("jcaptchaCode") != null) {
			userCaptchaResponse = req.getParameter("jcaptchaCode");
		}

		logger.debug("userResponse: " + userCaptchaResponse);

		// Proceed with the remaining filters
		chain.doFilter(req, res);
	}
	
	/**
     * Enable/Disable jcaptcha
     *
     * @param jcaptchaEnabled
     */
    public void setJcaptchaEnabled(boolean jcaptchaEnabled) {
        this.jcaptchaEnabled = jcaptchaEnabled;
    }

	public String getUserCaptchaResponse() {
		return userCaptchaResponse;
	}

	public void setUserCaptchaResponse(String userCaptchaResponse) {
		this.userCaptchaResponse = userCaptchaResponse;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
}