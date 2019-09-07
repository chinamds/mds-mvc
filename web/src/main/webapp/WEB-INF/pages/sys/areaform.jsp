<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="areaDetail.title"/></title>
    <meta name="menu" content="AreaMenu"/>
    <meta name="heading" content="<fmt:message key='areaDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_treeview" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/sys/areaform.js"%>
</c:set>

<%-- <ul class="nav nav-tabs">
	<li><a href="${ctx}/sys/areas/">Area List</a></li>
	<li class="active"><a href="areaform?id=${area.id}&parent.id=${area.parent.id}"><fmt:message key="area.title"/><secure:hasPermission name="sys:area:edit">${not empty area.id?' Edit':' Add'}</secure:hasPermission><secure:lacksPermission name="sys:area:edit">Approve</secure:lacksPermission></a></li>
</ul><br/> --%>

<c:set var="delObject" scope="request"><fmt:message key="areaList.area"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="areaDetail.heading"/></h2>
    <fmt:message key="areaDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="area" method="post" action="areaform" cssClass="well"
           id="areaForm" onsubmit="return validateArea(this)">
<form:hidden path="id"/>
<form:hidden path="type"/>
<form:hidden path="createdBy"/>
<form:hidden path="dateAdded"/>
<form:hidden path="lastModifiedBy"/>
<form:hidden path="dateLastModified"/>
	<div class="form-group">
		<appfuse:label key="area.parent" styleClass="control-label"/>
		<%-- <sys:treeselector id="area" name="parent.id" value="${area.parent.id}" labelName="parent.name" labelValue="${area.parent.name}"
			title="Area" url="/sys/areas/treeData" extId="${area.id}" cssClass="required form-control" maxlength="20"/> --%>
		<form:hidden path="parent.id" id="parentId"/>
		<comm:ztreepicker id="parent" keyName="parentId" keyValue="${area.parent.id}" fieldName="parentName" fieldValue="${area.parent.fullName}"
			url="/sys/areas/treeData" extId="${area.parent.id}" cssClass="required form-control" maxlength="20"/>
	</div>
	<spring:bind path="area.code">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="area.code" styleClass="control-label"/>
        <form:input cssClass="form-control" path="code" id="code"  maxlength="255"/>
        <form:errors path="code" cssClass="help-block"/>
    </div>
    <spring:bind path="area.name">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="area.name" styleClass="control-label"/>
        <form:input cssClass="form-control" path="name" id="name"  maxlength="256"/>
        <form:errors path="name" cssClass="help-block"/>
    </div>
    <spring:bind path="area.remarks">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="area.remarks" styleClass="control-label"/>
       <%--  <form:input cssClass="form-control" path="remarks" id="remarks"  maxlength="1024"/> --%>
        <form:textarea path="remarks" id="remarks" row='3' cssClass="form-control"/>
        <form:errors path="remarks" cssClass="help-block"/>
    </div>
    <%-- <spring:bind path="area.type">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="area.type" styleClass="control-label"/>
        <form:input cssClass="form-control" path="type" id="type"  maxlength="1"/>
        <form:errors path="type" cssClass="help-block"/>
    </div> --%>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty area.id}">
            <button type="submit" class="btn btn-danger" id="delete" name="delete" onclick="bCancel=true;return confirmMessage(msgDelConfirm)">
                <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/>
            </button>
        </c:if>

        <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
        </button>
    </div>
</form:form>
</div>

<v:javascript formName="area" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['areaForm']).focus();
    });
</script>
