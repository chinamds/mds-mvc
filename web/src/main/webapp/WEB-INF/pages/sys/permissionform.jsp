<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="permissionDetail.title"/></title>
    <meta name="menu" content="PermissionMenu"/>
    <meta name="heading" content="<fmt:message key='permissionDetail.heading'/>"/>
</head>

<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/sys/permissionform.js"%>
</c:set>

<c:set var="delObject" scope="request"><fmt:message key="permissionList.permission"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="permissionDetail.heading"/></h2>
    <fmt:message key="permissionDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="permission" method="post" action="permissionform" cssClass="well"
           id="permissionForm" onsubmit="return validatePermission(this)">
<form:hidden path="id"/>
<form:hidden path="createdBy"/>
<form:hidden path="dateAdded"/>
<form:hidden path="lastModifiedBy"/>
<form:hidden path="dateLastModified"/>
    <spring:bind path="permission.name">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="permission.name" styleClass="control-label"/>
        <form:input cssClass="form-control" path="name" id="name"  maxlength="100"/>
        <form:errors path="name" cssClass="help-block"/>
    </div>
    <spring:bind path="permission.permission">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="permission.permission" styleClass="control-label"/>
        <form:hidden path="permission"/>
        <%-- <form:input cssClass="form-control" path="permission" id="permission"  maxlength="100"/> --%>
        <%-- <label for="selectActions" class="control-label"><fmt:message key="permission.permission"/></label> --%>
        <select class="form-control" id="selectActions" name="selectActions" multiple="multiple">
        	<c:forEach items="${userActions}" var="userAction">
	        	<c:choose>
				    <c:when test="${ fn:contains(permission.permissions, userAction) }">
				        <option selected="selected" value="${userAction}"><fmt:message key="${userAction.label}"/></option>
				    </c:when>
				    <c:otherwise>
				        <option value="${userAction}"><fmt:message key="${userAction.label}"/></option>
				    </c:otherwise>
				</c:choose>
				<%-- <option value="${userAction}"><fmt:message key="${userAction.label}"/></option> --%>
		    </c:forEach>
		</select>
        <form:errors path="permission" cssClass="help-block"/>
    </div>
    <spring:bind path="permission.show">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="permission.show" styleClass="control-label"/>
        <form:checkbox path="show" id="show" cssClass="checkbox"/>
        <form:errors path="show" cssClass="help-block"/>
    </div>
    <spring:bind path="permission.description">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="permission.description" styleClass="control-label"/>
        <%-- <form:input cssClass="form-control" path="description" id="description"  maxlength="1024"/> --%>
        <form:textarea path="description" id="description" row='3' cssClass="form-control"/>
        <form:errors path="description" cssClass="help-block"/>
    </div>

    <div class="form-group">
    	<secure:hasAnyPermissions name="sys:permissions:add,sys:permissions:edit">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        </secure:hasAnyPermissions>
        <secure:hasPermission name="sys:permissions:delete">
        <c:if test="${not empty permission.id}">
            <button type="submit" class="btn btn-danger" id="delete" name="delete" onclick="bCancel=true;return confirmMessage(msgDelConfirm)">
                <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/>
            </button>
        </c:if>
        </secure:hasPermission>

        <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
        </button>
    </div>
</form:form>
</div>

<v:javascript formName="permission" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>
