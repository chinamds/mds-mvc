/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.common.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import com.mds.aiotplayer.security.model.MdsAuthenticationToken;
import com.mds.aiotplayer.common.web.jcaptcha.JCaptcha;

public class MdsAuthenticationProvider extends DaoAuthenticationProvider {
	private CaptchaCaptureFilter captchaCaptureFilter;
	
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken token)
			throws AuthenticationException {
		super.additionalAuthenticationChecks(userDetails, token);

		MdsAuthenticationToken mdsToken = (MdsAuthenticationToken) token;
		/*String poem = LoginQuestion.getQuestions().get(token.getQuestionId());
		// 校验下一句的答案是否正确
		if (!poem.split("/")[1].equals(token.getAnswer())) {
			throw new BadAnswerException("the answer is wrong!");
		}*/
		if (!mdsToken.isValidCaptcha()) {
			if (!JCaptcha.validateResponse(captchaCaptureFilter.getRequest(), captchaCaptureFilter.getUserCaptchaResponse())) {
				throw new BadCredentialsException("Captcha does not match.");
			}
			resetCaptchaFields();
		}
		
		/*if (!mdsToken.isValidCaptcha()) {
			throw new BadCredentialsException("Captcha does not match.");
		}*/
	}

	/**
	 * Reset Captcha fields
	 */
	public void resetCaptchaFields() {
		captchaCaptureFilter.setUserCaptchaResponse(null);
	}

	public CaptchaCaptureFilter getCaptchaCaptureFilter() {
		return captchaCaptureFilter;
	}

	public void setCaptchaCaptureFilter(CaptchaCaptureFilter captchaCaptureFilter) {
		this.captchaCaptureFilter = captchaCaptureFilter;
	}
}
