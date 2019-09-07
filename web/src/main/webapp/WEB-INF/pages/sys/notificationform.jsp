<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="notificationDetail.title"/></title>
    <meta name="menu" content="NotificationMenu"/>
    <meta name="heading" content="<fmt:message key='notificationDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="notificationList.notification"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="notificationDetail.heading"/></h2>
    <fmt:message key="notificationDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="notification" method="post" action="notificationform" cssClass="well"
           id="notificationForm" onsubmit="return validateNotification(this)">
<form:hidden path="id"/>
    <spring:bind path="notification.content">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="notification.content" styleClass="control-label"/>
        <form:input cssClass="form-control" path="content" id="content"  maxlength="255"/>
        <form:errors path="content" cssClass="help-block"/>
    </div>
    <spring:bind path="notification.date">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="notification.date" styleClass="control-label"/>
        <form:input cssClass="form-control" path="date" id="date"  maxlength="255"/>
        <form:errors path="date" cssClass="help-block"/>
    </div>
    <spring:bind path="notification.read">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="notification.read" styleClass="control-label"/>
        <form:checkbox path="read" id="read" cssClass="checkbox"/>
        <form:errors path="read" cssClass="help-block"/>
    </div>
    <spring:bind path="notification.source">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="notification.source" styleClass="control-label"/>
        <form:input cssClass="form-control" path="source" id="source"  maxlength="255"/>
        <form:errors path="source" cssClass="help-block"/>
    </div>
    <spring:bind path="notification.title">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="notification.title" styleClass="control-label"/>
        <form:input cssClass="form-control" path="title" id="title"  maxlength="255"/>
        <form:errors path="title" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
    <form:select cssClass="form-control" path="user" items="userList" itemLabel="label" itemValue="value"/>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty notification.id}">
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

<v:javascript formName="notification" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['notificationForm']).focus();
    });
</script>
