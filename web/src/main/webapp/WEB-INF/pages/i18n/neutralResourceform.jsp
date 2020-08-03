<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="neutralResourceDetail.title"/></title>
    <meta name="menu" content="NeutralResourceMenu"/>
    <meta name="heading" content="<fmt:message key='neutralResourceDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="neutralResourceList.neutralResource"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="neutralResourceDetail.heading"/></h2>
    <fmt:message key="neutralResourceDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="neutralResource" method="post" action="neutralResourceform" cssClass="well"
           id="neutralResourceForm" onsubmit="return validateNeutralResource(this)">
<form:hidden path="id"/>
<form:hidden path="resourceClass"/>
    <%-- <spring:bind path="neutralResource.resourceClass">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="neutralResource.resourceClass" styleClass="control-label"/>
        <form:input cssClass="form-control" path="resourceClass" id="resourceClass"  maxlength="100"/>
        <form:errors path="resourceClass" cssClass="help-block"/>
    </div> --%>
    <spring:bind path="neutralResource.resourceKey">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="neutralResource.resourceKey" styleClass="control-label"/>
        <form:input cssClass="form-control" path="resourceKey" id="resourceKey"  maxlength="256"/>
        <form:errors path="resourceKey" cssClass="help-block"/>
    </div>
    <spring:bind path="neutralResource.value">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="neutralResource.value" styleClass="control-label"/>
        <%-- <form:input cssClass="form-control" path="value" id="value"  maxlength="255"/> --%>
        <form:textarea path="value" id="value" rows="10" cssClass="form-control"/>
        <form:errors path="value" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty neutralResource.id}">
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

<v:javascript formName="neutralResource" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['neutralResourceForm']).focus();
    });
</script>
