<%@ include file="/common/taglibs.jsp" %>

<head>
    <title><fmt:message key="login.title"/></title>
    <meta name="menu" content="Login"/>
    <meta name="decorator" content="default_login"/>
    <%-- <t:assets type="css"/> --%>
</head>
<body id="login">
    
<div class="col">
    <div class="jumbotron">
	     <h2><fmt:message key="login.welcome"/></h2>   
	     <label class="text-danger"><fmt:message key="login.warning"/></label>
	     <!-- hack to close the jumbotron div before -->
	</div>
	<form method="post" id="loginForm" action="<c:url value='/j_security_check'/>"
		onsubmit="saveUsername(this);return validateForm(this)" class="form-signin" autocomplete="off">
		<security:csrfInput />
		<h2 class="form-signin-heading">
			<fmt:message key="login.heading"/>
		</h2>
	<c:if test="${param.error != null}">
		<div class="alert alert-danger alert-dismissable">
			<fmt:message key="errors.password.mismatch"/>
		</div>
	</c:if>
		<input type="text" name="username" id="username" class="form-control"
			   placeholder="<fmt:message key="label.username"/>" required tabindex="1">
		<input type="password" class="form-control" name="password" id="password" tabindex="2"
			   placeholder="<fmt:message key="label.password"/>" required>
			   
	<%-- jcaptchaEnabled setting in JCaptchaValidateFilter  --%>
	<c:if test="${jcaptchaEnabled}">
			  <%-- <label for="jcaptchaCode"><fmt:message key="login.verificationcode"/></label> --%>
				  <!-- <span class="input-group-addon"><i class="fa fa-circle-o"></i></span> -->
		<input type="text" id="jcaptchaCode" name="jcaptchaCode"
				class="form-control" placeholder="<fmt:message key="label.verificationcode"/>" tabindex="3" required>                    
		<%-- <div class="input-group">              
			<img class="jcaptcha-btn jcaptcha-img mr-5" src="${ctx}/jcaptcha.jpg" title="<fmt:message key="login.changepicture.tip"/>">
			<div class="input-group-append">
				<button type="button" class="jcaptcha-btn btn btn-link"><fmt:message key="login.changepicture"/></button>
			</div> ${ctx}/jcaptcha.jpg
		</div> --%>
		<div class="row">         
		    <div class="col">
			     <img class="jcaptcha-btn jcaptcha-img" src="${ctx}/services/api/users/jcaptcha" title="<fmt:message key="login.changepicture.tip"/>">
			</div>
			<div class="col px-0">
			     <button type="button" class="jcaptcha-btn btn btn-link float-end"><fmt:message key="login.changepicture"/></button>
			</div>
		</div>
	</c:if>           

	<c:if test="${appConfig['rememberMeEnabled']}">
		<div class="form-check">
			<input class="form-check-input" type="checkbox" name="_spring_security_remember_me" id="rememberMe" tabindex="4"/>
			<label for="rememberMe" class="form-check-label"><fmt:message key="login.rememberMe"/></label>
		</div>
	</c:if>
		<input type="hidden" id="mobileDevice" name="mobileDevice" value="true" />

		<div class="row d-grid">
            <button type="submit" class="btn btn-lg btn-primary btn-block" name="login" tabindex="5">
                <fmt:message key='button.login'/>
            </button>
        </div>
	</form>

	<p>
		<fmt:message key="login.signup">
			<fmt:param><c:url value="/sys/signup"/></fmt:param>
		</fmt:message>
	</p>

	<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/login.js"%>
	</c:set>

	<p><fmt:message key="login.passwordHint"/></p>

	<p><fmt:message key="updatePassword.requestRecoveryTokenLink"/></p>
</div>

</body>