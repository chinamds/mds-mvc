<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="roleDetail.title"/></title>
    <meta name="menu" content="RoleMenu"/>
    <meta name="heading" content="<fmt:message key='roleDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_boostrap_treeview" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/roleMenuPermissionform.js"%>
</c:set>

<c:set var="delObject" scope="request"><fmt:message key="roleList.role"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="roleDetail.heading"/></h2>
    <fmt:message key="roleDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="role" method="post" action="roleMenuPermissionform" cssClass="well"
           id="roleMenuPermissionForm" onsubmit="return validateRole(this)">
<form:hidden path="id"/>
<form:hidden path="type"/>
<c:if test="${not empty role.organization}">
<form:hidden path="organization.id"/>
</c:if>
<form:hidden path="description"/>
<form:hidden path="createdBy"/>
<form:hidden path="dateAdded"/>
<form:hidden path="lastModifiedBy"/>
<form:hidden path="dateLastModified"/>
    <spring:bind path="role.name">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="role.name" styleClass="control-label"/>
        <form:input cssClass="form-control" path="name" id="name" readonly="true" maxlength="100"/>
        <form:errors path="name" cssClass="help-block"/>
    </div>
    <div class="form-group">
    	<div id="menuFunctionPermissionTree" >loading... ...</div>
    </div>

    <div class="form-group">
    	<input type="hidden" id="menuPermissions" name="menuPermissions" />
    	<secure:hasAnyPermissions name="sys:roles:add,sys:roles:edit">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false;getCheckedNodes();">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        </secure:hasAnyPermissions>

        <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
        </button>
    </div>
</form:form>
</div>

<v:javascript formName="role" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>
