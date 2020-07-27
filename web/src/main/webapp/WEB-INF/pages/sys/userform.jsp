<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.mds.aiotplayer.sys.model.RoleType" %>

<head>
    <title><fmt:message key="userProfile.title"/></title>
    <meta name="menu" content="UserMenu"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/userform.js"%>
</c:set>

<c:set var="delObject" scope="request"><fmt:message key="userList.user"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-2">
    <h2><fmt:message key="userProfile.heading"/></h2>
    <c:choose>
        <c:when test="${param.from == 'list'}">
            <p><fmt:message key="userProfile.admin.message"/></p>
        </c:when>
        <c:otherwise>
            <p><fmt:message key="userProfile.message"/></p>
        </c:otherwise>
    </c:choose>
</div>
<div class="col-sm-7">
    <spring:bind path="user.*">
        <c:if test="${not empty status.errorMessages}">
            <div class="alert alert-danger alert-dismissable">
                <a href="#" data-dismiss="alert" class="close">&times;</a>
                <c:forEach var="error" items="${status.errorMessages}">
                    <c:out value="${error}" escapeXml="false"/><br/>
                </c:forEach>
            </div>
        </c:if>
    </spring:bind>

    <form:form modelAttribute="user" method="post" action="userform" id="userForm" autocomplete="off"
               cssClass="well" onsubmit="return validateUser(this)">
        <form:hidden path="id"/>
        <form:hidden path="version"/>
        <input type="hidden" name="from" value="<c:out value="${param.from}"/>"/>
        <input type="hidden" name="method" value="<c:out value="${param.method}"/>"/>

        <spring:bind path="user.username">
        <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
        </spring:bind>
            <appfuse:label styleClass="control-label" key="user.username"/>
            <form:input cssClass="form-control" path="username" id="username"/>
            <form:errors path="username" cssClass="help-block"/>
            <c:if test="${pageContext.request.remoteUser == user.username}">
                <span class="help-block">
                    <a href="<c:url value="/sys/updatePassword" />"><fmt:message key='updatePassword.changePasswordLink'/></a>
                </span>
            </c:if>
        </div>

		<c:if test="${param.method == 'Add'}">
            <spring:bind path="user.password">
            <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
            </spring:bind>
                <appfuse:label styleClass="control-label" key="user.password"/>
                <form:password cssClass="form-control" path="password" id="password" showPassword="true"/>
                <form:errors path="password" cssClass="help-block"/>
            </div>
            <form:hidden path="passwordHint"/>
        </c:if>
		<spring:bind path="user.organization">
	    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
	    </spring:bind>
			<appfuse:label key="user.organization" styleClass="control-label"/>
			<input type="hidden" name="organizationId" id="organizationId" value="${user.organization.id}"/>
			<comm:ztreepicker id="organization" keyName="organizationId" keyValue="${user.organization.id}" fieldName="organizationName" fieldValue="${user.organization.fullName}"
				url="/sys/organizations/treeData" extId="${user.organization.id}" cssClass="required form-control" maxlength="200"/>
			<form:errors path="organization" cssClass="help-block"/>
		</div>
        <c:if test="${param.method != 'Add'}">
        <form:hidden path="password"/>
        <form:hidden path="passwordHint"/>
        <%-- <spring:bind path="user.passwordHint">
        <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
        </spring:bind>
            <appfuse:label styleClass="control-label" key="user.passwordHint"/>
            <form:input cssClass="form-control" path="passwordHint" id="passwordHint"/>
            <form:errors path="passwordHint" cssClass="help-block"/>
        </div> --%>
        </c:if>
        <div class="row">
            <spring:bind path="user.firstName">
            <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
            </spring:bind>
                <appfuse:label styleClass="control-label" key="user.firstName"/>
                <form:input cssClass="form-control" path="firstName" id="firstName" maxlength="50"/>
                <form:errors path="firstName" cssClass="help-block"/>
            </div>
            <spring:bind path="user.lastName">
            <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
            </spring:bind>
                <appfuse:label styleClass="control-label" key="user.lastName"/>
                <form:input cssClass="form-control" path="lastName" id="lastName" maxlength="50"/>
                <form:errors path="lastName" cssClass="help-block"/>
            </div>
        </div>
        <div class="row">
            <spring:bind path="user.email">
            <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
            </spring:bind>
                <appfuse:label styleClass="control-label" key="user.email"/>
                <form:input cssClass="form-control" path="email" id="email"/>
                <form:errors path="email" cssClass="help-block"/>
            </div>
            <div class="col-sm-6 form-group">
                <appfuse:label styleClass="control-label" key="user.phoneNumber"/>
                <form:input cssClass="form-control" path="phoneNumber" id="phoneNumber"/>
            </div>
        </div>
        <div class="form-group">
            <appfuse:label styleClass="control-label" key="user.website"/>
            <form:input cssClass="form-control" path="website" id="website"/>
        </div>
	<c:choose>
    <c:when test="${param.from == 'list' or param.method == 'Add'}">
    	<secure:hasRoleType name='${RoleType.sa.toString()}'>
        <div class="form-group">
            <label class="control-label"><fmt:message key="userProfile.accountSettings"/></label>
            <label class="checkbox-inline">
                <form:checkbox path="enabled" id="enabled"/>
                <fmt:message key="user.enabled"/>
            </label>

            <label class="checkbox-inline">
                <form:checkbox path="accountExpired" id="accountExpired"/>
                <fmt:message key="user.accountExpired"/>
            </label>

            <label class="checkbox-inline">
                <form:checkbox path="accountLocked" id="accountLocked"/>
                <fmt:message key="user.accountLocked"/>
            </label>

            <label class="checkbox-inline">
                <form:checkbox path="credentialsExpired" id="credentialsExpired"/>
                <fmt:message key="user.credentialsExpired"/>
            </label>
        </div>
        </secure:hasRoleType>
        <div class="form-group">
            <label for="userRoles" class="control-label"><fmt:message key="userProfile.assignRoles"/></label>
            <select id="userRoles" name="userRoles" multiple="true" class="form-control">
                <c:forEach items="${user.roles}" var="role">
                <option value="${role.name}" selected="selected">${role.name}</option>
                </c:forEach>
            </select>
            <secure:lacksRoleTypes name='${RoleType.sa.toString()}'>
           	<form:hidden path="enabled"/>
            <form:hidden path="accountExpired"/>
            <form:hidden path="accountLocked"/>
            <form:hidden path="credentialsExpired"/>
            </secure:lacksRoleTypes>
        </div>
    </c:when>
    <c:when test="${not empty user.username}">
        <div class="form-group">
            <%-- <label class="control-label"><fmt:message key="user.roles"/>:</label>
            <div class="readonly">
                <c:forEach var="role" items="${user.roleList}" varStatus="status">
                    <c:out value="${role.label}"/><c:if test="${!status.last}">,</c:if>
                    <input type="hidden" name="userRoles" value="<c:out value="${role.label}"/>"/>
                </c:forEach>
            </div> --%>
            <form:hidden path="enabled"/>
            <form:hidden path="accountExpired"/>
            <form:hidden path="accountLocked"/>
            <form:hidden path="credentialsExpired"/>
        </div>
    </c:when>
</c:choose>
        <div class="form-group">
        	<secure:hasAnyPermissions name="sys:users:add,sys:users:edit">
            <button type="submit" class="btn btn-primary" name="save" onclick="bCancel=false">
                <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
            </button>
            </secure:hasAnyPermissions>

			<secure:hasPermission name="sys:users:delete">
            <c:if test="${param.from == 'list' and param.method != 'Add'}">
              <button type="submit" class="btn btn-default" name="delete" onclick="bCancel=true;return confirmMessage(msgDelConfirm)">
                  <i class="fa fa-trash"></i> <fmt:message key="button.delete"/>
              </button>
            </c:if>
            </secure:hasPermission>

            <button type="submit" class="btn btn-default" name="cancel" onclick="bCancel=true">
                <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
            </button>
        </div>
    </form:form>
</div>

<%-- <c:set var="scripts" scope="request">
<script type="text/javascript">
// This is here so we can exclude the selectAll call when roles is hidden
function onFormSubmit(theForm) {
    return validateUser(theForm);
}
</script>
<%@ include file="/static/scripts/sys/userform.js"%>
</c:set> --%>

<v:javascript formName="user" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value="/static/scripts/validator.jsp"/>"></script>

