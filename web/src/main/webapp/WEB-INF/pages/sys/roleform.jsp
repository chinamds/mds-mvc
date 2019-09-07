<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.mds.sys.model.RoleType" %>

<head>
    <title><fmt:message key="roleDetail.title"/></title>
    <meta name="menu" content="RoleMenu"/>
    <meta name="heading" content="<fmt:message key='roleDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/sys/roleform.js"%>
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
<form:form modelAttribute="role" method="post" action="roleform" cssClass="well"
           id="roleForm" onsubmit="return validateRole(this)">
<form:hidden path="id"/>
<form:hidden path="createdBy"/>
<form:hidden path="dateAdded"/>
<form:hidden path="lastModifiedBy"/>
<form:hidden path="dateLastModified"/>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <%-- <form:select cssClass="form-control" path="company" items="companyList" itemLabel="label" itemValue="value"/> --%>
    <spring:bind path="role.name">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="role.name" styleClass="control-label"/>
        <form:input cssClass="form-control" path="name" id="name"  maxlength="100"/>
        <form:errors path="name" cssClass="help-block"/>
    </div>
    <spring:bind path="role.organization">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
		<appfuse:label key="role.organization" styleClass="control-label"/>
		<input type="hidden" name="organizationId" id="organizationId" value="${role.organization.id}"/>
		<comm:ztreepicker id="organization" keyName="organizationId" keyValue="${role.organization.id}" fieldName="organizationName" fieldValue="${role.organization.fullName}"
			url="/sys/organizations/treeData" extId="${role.organization.id}" cssClass="required form-control" maxlength="200"/>
		<form:errors path="organization" cssClass="help-block"/>
	</div>
    <spring:bind path="role.type">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="role.type" styleClass="control-label"/>
        <%-- <form:input cssClass="form-control" path="type" id="type"  maxlength="255"/> --%>
        <select id="type" name="type" class="form-control">
            <c:forEach items="${roleTypes}" var="roleType">
            <option value="${roleType.roleType}" ${ roleType.roleType eq role.type ? 'selected' : ''}>${roleType.info}</option>
            </c:forEach>
        </select>
        <form:errors path="type" cssClass="help-block"/>
    </div>
    <div class="rolegalleries form-group ${(role.type == RoleType.ga or role.type == RoleType.gu or role.type == RoleType.gg) ? '' : 'hidden d-none'}">
		 <label for="roleGalleries" class="control-label"><fmt:message key="role.galleries"/></label>
		 <select id="roleGalleries" name="roleGalleries" class="form-control">
			 <c:forEach items="${role.galleries}" var="gallery">
				 <option value="${gallery.id}" selected="selected">${gallery.title}</option>
			 </c:forEach>
		 </select>
   </div>
    <spring:bind path="role.description">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="role.description" styleClass="control-label"/>
        <%-- <form:input cssClass="form-control" path="description" id="description"  maxlength="1024"/> --%>
        <form:textarea path="description" id="description" row='3' cssClass="form-control"/>
        <form:errors path="description" cssClass="help-block"/>
    </div>

    <div class="form-group">
    	<secure:hasAnyPermissions name="sys:roles:add,sys:roles:edit">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        </secure:hasAnyPermissions>
        <secure:hasPermission name="sys:roles:delete">
        <c:if test="${not empty role.id}">
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

<v:javascript formName="role" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>
