<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><fmt:message key="userProfile.title"/></title>
</head>

<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/users/loginUser/editForm.js"%>
</c:set>

<div class="col-sm-10">
	<%@include file="nav.jspf"%>
	
    <form:form id="editForm" method="post" modelAttribute="user" cssClass="form-horizontal">
        <form:hidden path="id"/>
        <c:if test="${op eq 'userProfile.viewprofile'}">
            <h4 class="hr"><fmt:message key="userProfile.heading"/></h4>
        </c:if>
        
        <div class="form-group">
            <form:label path="username" cssClass="control-label"><fmt:message key="user.username"/></form:label>
            <fmt:message key="user.username.tip" var="usernametip"/>
            <form:input path="username" cssClass="form-control" placeholder="${usernametip}" />
        </div>
        
        <div class="form-group">
            <form:label path="email" cssClass="control-label"><fmt:message key="user.email"/></form:label>
            <fmt:message key="user.email.tip" var="useremailtip"/>
            <form:input path="email" cssClass="form-control" placeholder="${useremailtip}"/>
        </div>

        <div class="form-group">
            <form:label path="mobile" cssClass="control-label"><fmt:message key="user.mobile"/></form:label>
            <fmt:message key="user.mobile.tip" var="usermobiletip"/>
            <form:input path="mobile" cssClass="form-control" placeholder="${usermobiletip}"/>
        </div>

        <c:if test="${op eq 'userProfile.viewprofile'}">
            <div class="form-group">
                <form:label path="dateAdded" cssClass="control-label"><fmt:message key="user.dateAdded"/></form:label>
                <form:input path="dateAdded" cssClass="form-control"/>
            </div>

            <div class="form-group">
	            <label class="control-label"><fmt:message key="userProfile.accountSettings"/></label>
	            <label>
	                <form:checkbox path="enabled" id="enabled"/>
	                <fmt:message key="user.enabled"/>
	            </label>
	
	            <label>
	                <form:checkbox path="accountExpired" id="accountExpired"/>
	                <fmt:message key="user.accountExpired"/>
	            </label>
	
	            <label>
	                <form:checkbox path="accountLocked" id="accountLocked"/>
	                <fmt:message key="user.accountLocked"/>
	            </label>
	
	            <label>
	                <form:checkbox path="credentialsExpired" id="credentialsExpired"/>
	                <fmt:message key="user.credentialsExpired"/>
	            </label>
	        </div>
        </c:if>

        <div class="clearfix"></div>
        <c:if test="${op eq 'userProfile.viewprofile' and not empty lastOnline}">
            <br/>
            <h4 class="hr"><fmt:message key="lastOnlineUsers.title"/></h4>
            <div class="form-group">
                <label class="control-label"><fmt:message key="onlineUser.lasthostip"/></label>
                <input type="text" value="${lastOnline.host}">
            </div>
            <div class="form-group">
                <label class="control-label"><fmt:message key="onlineUser.lastlogintime"/></label>
                <input type="text" value="<spring:eval expression='lastOnline.lastLoginTimestamp'/>">
            </div>
            <div class="form-group">
                <label class="control-label"><fmt:message key="onlineUser.lastlogouttime"/></label>
                <input type="text" value="<spring:eval expression='lastOnline.lastStopTimestamp'/>">
            </div>
            <div class="form-group">
                <label class="control-label"><fmt:message key="onlineUser.logincount"/></label>
                <input type="text" value="${lastOnline.loginCount}">
            </div>
            <div class="form-group">
                <label class="control-label"><fmt:message key="onlineUser.onlineduration"/></label>
                <input type="text" value="<pretty:prettySecond seconds="${lastOnline.totalOnlineTime}"/>"/>
            </div>
        </c:if>

        <c:if test="${op eq 'userProfile.editprofile'}">
            <c:set var="icon" value="glyphicon glyphicon-edit"/>
        </c:if>

        <div class="form-group">
			<button type="submit" class="btn btn-primary"><i class="${icon}"></i> <fmt:message key="${op}"/>
			</button>
        </div>
    </form:form>
</div>
