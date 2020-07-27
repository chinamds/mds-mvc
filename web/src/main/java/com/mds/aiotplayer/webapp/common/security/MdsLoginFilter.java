package com.mds.aiotplayer.webapp.common.security;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.mds.aiotplayer.security.model.MdsAuthenticationToken;
import com.mds.aiotplayer.util.Utils;
import com.mds.aiotplayer.common.web.jcaptcha.JCaptcha;

public class MdsLoginFilter extends UsernamePasswordAuthenticationFilter {
	
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {

		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String username = obtainUsername(request).toUpperCase().trim();
		String password = obtainPassword(request);
		String jcaptchaCode = obtainJcaptchaCode(request);
		//获取用户输入的下一句答案
		//String answer = obtainAnswer(request);
		//获取问题Id(即: hashTable的key)
		//Integer questionId = obtainQuestionId(request);
		boolean jcaptchaOk = false;
		if (jcaptchaEnabled == false || !"post".equals(request.getMethod().toLowerCase())) {
			jcaptchaOk = true;
        /*}else {
        	jcaptchaOk =  JCaptcha.validateResponse(request, jcaptchaCode);*/
        }

		//这里将原来的UsernamePasswordAuthenticationToken换成我们自定义的MdsAuthenticationToken
		MdsAuthenticationToken authRequest = new MdsAuthenticationToken(
				username, password, jcaptchaOk, obtainMobileDevice(request));

		//这里就将token传到后续验证环节了
		setDetails(request, authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}

	protected String obtainAnswer(HttpServletRequest request) {
		return request.getParameter(answerParameter);
	}

	protected Integer obtainQuestionId(HttpServletRequest request) {
		return Integer.parseInt(request.getParameter(questionIdParameter));
	}
	
	protected String obtainJcaptchaCode(HttpServletRequest request) {
		return request.getParameter(jcaptchaCodeParameter);
	}
	
	protected boolean obtainMobileDevice(HttpServletRequest request) {
		return Utils.getBoolParameter(request, mobileDeviceParameter);
	}

	private String questionIdParameter = "questionId";
	private String answerParameter = "answer";
	private String jcaptchaCodeParameter = "jcaptchaCode";
	private String mobileDeviceParameter = "mobileDevice";
	private boolean jcaptchaEnabled = true;
	
    /**
     * Enable/Disable jcaptcha
     *
     * @param jcaptchaEnabled
     */
    public void setJcaptchaEnabled(boolean jcaptchaEnabled) {
        this.jcaptchaEnabled = jcaptchaEnabled;
    }
	
	public String getJcaptchaCodeParameter() {
		return jcaptchaCodeParameter;
	}

	public void setJcaptchaCodeParameter(String jcaptchaCodeParameter) {
		this.jcaptchaCodeParameter = jcaptchaCodeParameter;
	}
	
	public String getMobileDeviceParameter() {
		return mobileDeviceParameter;
	}

	public void setMobileDeviceParameter(String mobileDeviceParameter) {
		this.mobileDeviceParameter = mobileDeviceParameter;
	}

	public String getQuestionIdParameter() {
		return questionIdParameter;
	}

	public void setQuestionIdParameter(String questionIdParameter) {
		this.questionIdParameter = questionIdParameter;
	}

	public String getAnswerParameter() {
		return answerParameter;
	}

	public void setAnswerParameter(String answerParameter) {
		this.answerParameter = answerParameter;
	}

}
