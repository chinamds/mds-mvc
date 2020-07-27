<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.mds.aiotplayer.core.ResourceId" %>
<%@ page import="com.mds.aiotplayer.sys.model.RoleType" %>

<head>
    <title><fmt:message key="menuFunctionDetail.title"/></title>
    <meta name="menu" content="MenuFunctionMenu"/>
    <meta name="heading" content="<fmt:message key='menuFunctionDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/sys/menuFunctionform.js"%>
</c:set>

<c:set var="delObject" scope="request"><fmt:message key="menuFunctionList.menuFunction"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="menuFunctionDetail.heading"/></h2>
    <fmt:message key="menuFunctionDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="menuFunction" method="post" action="menuFunctionform" cssClass="well"
           id="menuFunctionForm" onsubmit="return validateMenu(this)">
<form:hidden path="id"/>
<form:hidden path="action"/>
<form:hidden path="isActiviti"/>
<form:hidden path="createdBy"/>
<form:hidden path="dateAdded"/>
<form:hidden path="lastModifiedBy"/>
<form:hidden path="dateLastModified"/>
	<div class="form-group">
		<appfuse:label key="menuFunction.parent" styleClass="control-label"/>
		<%-- <sys:treeselector id="menuFunction" name="parent.id" value="${menuFunction.parent.id}" labelName="parent.name" labelValue="${menuFunction.parent.name}"
			title="Menu" url="/sys/menuFunctions/treeData" extId="${menuFunction.id}" cssClass="required form-control" maxlength="20"/> --%>
		<form:hidden path="parent.id" id="parentId"/>
		<comm:ztreepicker id="parent" keyName="parentId" keyValue="${menuFunction.parent.id}" fieldName="parentName" fieldValue="${menuFunction.parent.fullCode}"
			url="/sys/menuFunctions/treeData" extId="${menuFunction.parent.id}" cssClass="required form-control" maxlength="200"/>
	</div>
	<spring:bind path="menuFunction.resourceId">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="menuFunction.resourceId" styleClass="control-label"/>
        <select id="resourceId" name="resourceId" class="form-control">
            <c:forEach items="${resourceIds}" var="rId">
            <option value="${rId.resourceId}" ${ rId.resourceId eq menuFunction.resourceId ? 'selected' : ''}>${rId.info}</option>
            </c:forEach>
        </select>
        <form:errors path="resourceId" cssClass="help-block"/>
    </div>
    <spring:bind path="menuFunction.code">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="menuFunction.code" styleClass="control-label"/>
        <form:input cssClass="form-control" path="code" id="code"  maxlength="100"/>
        <form:errors path="code" cssClass="help-block"/>
    </div>
    <%-- <spring:bind path="menuFunction.action">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="menuFunction.action" styleClass="control-label"/>
        <form:input cssClass="form-control" path="action" id="action"  maxlength="100"/>
        <form:errors path="action" cssClass="help-block"/>
    </div> --%>
    <spring:bind path="menuFunction.href">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="menuFunction.href" styleClass="control-label"/>
        <form:input cssClass="form-control" path="href" id="href"  maxlength="1024"/>
        <form:errors path="href" cssClass="help-block"/>
    </div>
    <spring:bind path="menuFunction.icon">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="menuFunction.icon" styleClass="control-label"/>
        <form:input cssClass="form-control" path="icon" id="icon"  maxlength="1024"/>
        <form:errors path="icon" cssClass="help-block"/>
    </div>
    <spring:bind path="menuFunction.sort">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="menuFunction.sort" styleClass="control-label"/>
        <form:input cssClass="form-control" path="sort" id="sort"  maxlength="255"/>
        <form:errors path="sort" cssClass="help-block"/>
    </div>
    <%-- <spring:bind path="menuFunction.isActiviti">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="menuFunction.isActiviti" styleClass="control-label"/>
        <form:input cssClass="form-control" path="isActiviti" id="isActiviti"  maxlength="1"/>
        <form:checkbox path="isActiviti" id="isActiviti" cssClass="checkbox"/>
        <form:errors path="isActiviti" cssClass="help-block"/>
    </div> --%>
    <spring:bind path="menuFunction.isShow">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="menuFunction.isShow" styleClass="control-label"/>
        <form:checkbox path="isShow" id="isShow" cssClass="checkbox"/>
        <form:errors path="isShow" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <%-- <form:select cssClass="form-control" path="parent" items="parentList" itemLabel="label" itemValue="value"/> --%>
    <div class="form-group">
        <label for="menuPermissions" class="control-label"><fmt:message key="menuFunction.permission"/></label>
        <select class="form-control" id="menuPermissions" name="menuPermissions" multiple="multiple">
	        <c:forEach items="${menuFunction.menuFunctionPermissions}" var="menuPermission">
		        <option selected="selected" value="${menuPermission.permission.id}">${menuPermission.permission.name}</option>
		    </c:forEach>
		</select>
    </div>
    <spring:bind path="menuFunction.target">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="menuFunction.target" styleClass="control-label"/>
        <%-- <form:input cssClass="form-control" path="target" id="target"  maxlength="10"/> --%>
        <select id="target" name="target" class="form-control">
            <c:forEach items="${menuTargets}" var="menuTarget">
            <option value="${menuTarget}" ${ menuTarget eq menuFunction.target ? 'selected' : ''}><fmt:message key="${menuTarget.info}"/></option>
            </c:forEach>
        </select>
        <form:errors path="target" cssClass="help-block"/>
    </div>
    <%-- <spring:bind path="menuFunction.title">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="menuFunction.title" styleClass="control-label"/>
        <form:input cssClass="form-control" path="title" id="title"  maxlength="256"/>
        <form:errors path="title" cssClass="help-block"/>
    </div>
    <spring:bind path="menuFunction.description">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="menuFunction.description" styleClass="control-label"/>
        <form:input cssClass="form-control" path="description" id="description"  maxlength="1024"/>
        <form:textarea path="description" id="description" row='3' cssClass="form-control"/>
        <form:errors path="description" cssClass="help-block"/>
    </div> --%>
    <div class="form-group">
        <appfuse:label key="menuFunction.title" styleClass="control-label"/>
        <fmt:message key="${menuFunction.title}" var="menuTitle"/>
        <input id="title" name="title" class="form-control" readonly="true" value="${menuTitle}"/>
    </div>
    <div class="form-group">
        <appfuse:label key="menuFunction.description" styleClass="control-label"/>
        <fmt:message key="${menuFunction.description}" var="menuDescription"/>
        <c:if test="${not empty menuDescription}">
        <textarea rows="3" id="description" class="form-control" readonly="true">${menuDescription}</textarea>
        </c:if>
        <c:if test="${empty menuDescription}">
        <textarea rows="3" id="description" class="form-control" readonly="true">${menuFunction.description}</textarea>
        </c:if>
    </div>

    <div class="form-group">
    	<secure:hasRoleType name='${RoleType.sa.toString()}'>
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        </secure:hasRoleType>
        <secure:lacksRoleTypes name='${RoleType.sa.toString()}'>
    	<secure:hasAnyPermissions name="sys:menuFunctions:add,sys:menuFunctions:edit">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-ok icon-white"></i> <fmt:message key="button.save"/>
        </button>
        </secure:hasAnyPermissions>
        </secure:lacksRoleTypes>
        <secure:hasPermission name="sys:menuFunctions:delete">
        <c:if test="${not empty menuFunction.id}">
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

<v:javascript formName="menuFunction" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>
