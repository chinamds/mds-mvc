<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="notificationTemplateDetail.title"/></title>
    <meta name="menu" content="NotificationTemplateMenu"/>
    <meta name="heading" content="<fmt:message key='notificationTemplateDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="notificationTemplateList.notificationTemplate"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="notificationTemplateDetail.heading"/></h2>
    <fmt:message key="notificationTemplateDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="notificationTemplate" method="post" action="notificationTemplateform" cssClass="well"
           id="notificationTemplateForm" onsubmit="return validateNotificationTemplate(this)">
<form:hidden path="id"/>
    <spring:bind path="notificationTemplate.deleted">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="notificationTemplate.deleted" styleClass="control-label"/>
        <form:checkbox path="deleted" id="deleted" cssClass="checkbox"/>
        <form:errors path="deleted" cssClass="help-block"/>
    </div>
    <spring:bind path="notificationTemplate.name">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="notificationTemplate.name" styleClass="control-label"/>
        <form:input cssClass="form-control" path="name" id="name"  maxlength="100"/>
        <form:errors path="name" cssClass="help-block"/>
    </div>
    <spring:bind path="notificationTemplate.source">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="notificationTemplate.source" styleClass="control-label"/>
        <form:input cssClass="form-control" path="source" id="source"  maxlength="255"/>
        <form:errors path="source" cssClass="help-block"/>
    </div>
    <spring:bind path="notificationTemplate.template">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="notificationTemplate.template" styleClass="control-label"/>
        <form:input cssClass="form-control" path="template" id="template"  maxlength="1024"/>
        <form:errors path="template" cssClass="help-block"/>
    </div>
    <spring:bind path="notificationTemplate.title">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="notificationTemplate.title" styleClass="control-label"/>
        <form:input cssClass="form-control" path="title" id="title"  maxlength="1024"/>
        <form:errors path="title" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty notificationTemplate.id}">
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

<v:javascript formName="notificationTemplate" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['notificationTemplateForm']).focus();
    });
</script>
