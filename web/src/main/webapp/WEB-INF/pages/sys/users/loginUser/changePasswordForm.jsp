<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><fmt:message key="updatePassword.title"/></title>
</head>

<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/users/loginUser/changePasswordForm.js"%>
</c:set>

<div class="col-sm-10">
	<%@include file="nav.jspf"%>
    <sys:showMessage/>

    <form id="editForm" method="post" class="form-horizontal">
        <div class="form-group">
            <label class="control-label"><fmt:message key="user.username"/></label>
            <input type="text" name="username" class="form-control" id="username" value="<c:out value="${user.username}" escapeXml="true"/>" required>
        </div>

        <div class="form-group">
            <label for="oldPassword" class="control-label"><fmt:message key="updatePassword.currentPassword.label"/></label>
            <input type="password" id="oldPassword" name="oldPassword" class="form-control"  required autofocus/>
        </div>

        <div class="form-group">
            <label for="newPassword1" class="control-label"><fmt:message key="updatePassword.newPassword.label"/></label>
            <fmt:message key="updatePassword.newPassword.tip" var="newPasswordtip"/>
            <input type="password" id="newPassword1" name="newPassword1"
                        class="form-control" placeholder="${newPasswordtip}"  required/>
        </div>
        <div class="form-group">
            <label for="newPassword2" class="control-label"><fmt:message key="updatePassword.confirmPassword.label"/></label>
            <fmt:message key="updatePassword.confirmPassword.tip" var="confirmPasswordtip"/>
            <input type="password" id="newPassword2" name="newPassword2" class="form-control" placeholder="${confirmPasswordtip}" required/>
        </div>

        <div class="form-group">
                <button type="submit" class="btn btn-primary">
                    <i class="fa fa-key"></i>
                        <fmt:message key='updatePassword.changePasswordButton'/>
                </button>
         </div>
    </form>
</div>
