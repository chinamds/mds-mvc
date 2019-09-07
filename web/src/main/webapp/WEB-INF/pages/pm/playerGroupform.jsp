<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="playerGroupDetail.title"/></title>
    <meta name="menu" content="PlayerGroupMenu"/>
    <meta name="heading" content="<fmt:message key='playerGroupDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/pm/playerGroupform.js"%>
</c:set>

<c:set var="delObject" scope="request"><fmt:message key="playerGroupList.playerGroup"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="playerGroupDetail.heading"/></h2>
    <fmt:message key="playerGroupDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="playerGroup" method="post" action="playerGroupform" cssClass="well"
           id="playerGroupForm" onsubmit="return validatePlayerGroup(this)">
<form:hidden path="id"/>
<form:hidden path="createdBy"/>
<form:hidden path="dateAdded"/>
<form:hidden path="lastModifiedBy"/>
<form:hidden path="dateLastModified"/>
	<div class="form-group">
		<appfuse:label key="playerGroup.parent" styleClass="control-label"/>
		<form:hidden path="parent.id" id="parentId"/>
		<comm:ztreepicker id="parent" keyName="parentId" keyValue="${playerGroup.parent.id}" fieldName="parentName" fieldValue="${playerGroup.parent.fullName}"
			 url="/services/api/playerGroups/treeSelector" extId="${playerGroup.id}" cssClass="required form-control" maxlength="20"/>
	</div>
	<spring:bind path="playerGroup.code">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerGroup.code" styleClass="control-label"/>
        <form:input cssClass="form-control" path="code" id="code"  maxlength="38"/>
        <form:errors path="code" cssClass="help-block"/>
    </div>
    <spring:bind path="playerGroup.name">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerGroup.name" styleClass="control-label"/>
        <form:input cssClass="form-control" path="name" id="name"  maxlength="256"/>
        <form:errors path="name" cssClass="help-block"/>
    </div>
    <spring:bind path="playerGroup.organization">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
		<appfuse:label key="playerGroup.organization" styleClass="control-label"/>
		<input type="hidden" name="organizationId" id="organizationId" value="${playerGroup.organization.id}"/>
		<comm:ztreepicker id="organization" keyName="organizationId" keyValue="${playerGroup.organization.id}" fieldName="organizationName" fieldValue="${playerGroup.organization.fullName}"
			url="/sys/organizations/treeData" extId="${playerGroup.organization.id}" cssClass="required form-control" maxlength="200"/>
		<form:errors path="organization" cssClass="help-block"/>
	</div>
    <spring:bind path="playerGroup.description">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="playerGroup.description" styleClass="control-label"/>
        <form:input cssClass="form-control" path="description" id="description"  maxlength="256"/>
        <form:errors path="description" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty playerGroup.id}">
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

<v:javascript formName="playerGroup" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<!-- <script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['playerGroupForm']).focus();
    });
</script> -->
