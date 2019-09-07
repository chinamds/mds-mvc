package com.mds.security.model;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class MdsAuthenticationToken extends
		UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = 5414106440823275021L;

	public MdsAuthenticationToken(String principal, String credentials,
			Integer questionId, String answer) {
		super(principal, credentials);
		this.answer = answer;
		this.questionId = questionId;
	}
	
	public MdsAuthenticationToken(String principal, String credentials, boolean validCaptcha, boolean mobileDevice) {
		super(principal, credentials);
		this.validCaptcha = validCaptcha;
		this.mobileDevice = mobileDevice;
	}

	private String answer;
	private Integer questionId;
	private boolean mobileDevice;
	private boolean validCaptcha;

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Integer getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}
	
	public boolean isMobileDevice() {
		return mobileDevice;
	}
	
	public void setMobileDevice(boolean mobileDevice) {
		this.mobileDevice = mobileDevice;
	}
	
	public boolean isValidCaptcha() {
		return validCaptcha;
	}

	public void setValidCaptcha(boolean validCaptcha) {
		this.validCaptcha = validCaptcha;
	}
}
