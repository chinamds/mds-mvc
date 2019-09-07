<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="dictDetail.title"/></title>
    <meta name="menu" content="DictMenu"/>
    <meta name="heading" content="<fmt:message key='dictDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="dictList.dict"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="dictDetail.heading"/></h2>
    <fmt:message key="dictDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="dict" method="post" action="dictform" cssClass="well"
           id="dictForm" onsubmit="return validateDict(this)">
<form:hidden path="id"/>
<form:hidden path="createdBy"/>
<form:hidden path="dateAdded"/>
<form:hidden path="lastModifiedBy"/>
<form:hidden path="dateLastModified"/>
	<spring:bind path="dict.category">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
		<appfuse:label key="dict.category" styleClass="control-label"/>
        <%-- <form:input cssClass="form-control" path="category" id="category"  maxlength="100"/> --%>
        <form:select path="category" id="category"  items="${categories}" cssClass="form-control"/>
        <form:errors path="category" cssClass="help-block"/>
    </div>
	<spring:bind path="dict.word">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
		<appfuse:label key="dict.word" styleClass="control-label"/>
        <form:input cssClass="form-control" path="word" id="word"  maxlength="100"/>
        <form:errors path="word" cssClass="help-block"/>
    </div>
	<spring:bind path="dict.value">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
		<appfuse:label key="dict.value" styleClass="control-label"/>
        <%-- <form:input cssClass="form-control" path="value" id="value"  maxlength="1024"/> --%>
        <form:textarea path="value" id="value" row='3' cssClass="form-control"/>
        <form:errors path="value" cssClass="help-block"/>
    </div>
	<spring:bind path="dict.description">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
		<appfuse:label key="dict.description" styleClass="control-label"/>
        <%-- <form:input cssClass="form-control" path="description" id="description"  maxlength="1024"/> --%>
        <form:textarea path="description" id="description" row='3' cssClass="form-control"/>
        <form:errors path="description" cssClass="help-block"/>
    </div>
	<spring:bind path="dict.sort">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
		<appfuse:label key="dict.sort" styleClass="control-label"/>
        <form:input cssClass="form-control" path="sort" id="sort"  maxlength="100"/>
        <form:errors path="sort" cssClass="help-block"/>
    </div>
	<div class="form-group">
		<button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
			<i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
		</button>
		<c:if test="${not empty dict.id}">
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

<v:javascript formName="dict" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['dictForm']).focus();
    });
</script>

