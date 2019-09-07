<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="taskDefinitionDetail.title"/></title>
    <meta name="menu" content="TaskDefinitionMenu"/>
    <meta name="heading" content="<fmt:message key='taskDefinitionDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="taskDefinitionList.taskDefinition"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="taskDefinitionDetail.heading"/></h2>
    <fmt:message key="taskDefinitionDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="taskDefinition" method="post" action="taskDefinitionform" cssClass="well"
           id="taskDefinitionForm" onsubmit="return validateTaskDefinition(this)">
<form:hidden path="id"/>
    <spring:bind path="taskDefinition.createdBy">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="taskDefinition.createdBy" styleClass="control-label"/>
        <form:input cssClass="form-control" path="createdBy" id="createdBy"  maxlength="100"/>
        <form:errors path="createdBy" cssClass="help-block"/>
    </div>
    <spring:bind path="taskDefinition.dateAdded">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="taskDefinition.dateAdded" styleClass="control-label"/>
        <form:input cssClass="form-control" path="dateAdded" id="dateAdded"  maxlength="19"/>
        <form:errors path="dateAdded" cssClass="help-block"/>
    </div>
    <spring:bind path="taskDefinition.dateLastModified">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="taskDefinition.dateLastModified" styleClass="control-label"/>
        <form:input cssClass="form-control" path="dateLastModified" id="dateLastModified"  maxlength="19"/>
        <form:errors path="dateLastModified" cssClass="help-block"/>
    </div>
    <spring:bind path="taskDefinition.lastModifiedBy">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="taskDefinition.lastModifiedBy" styleClass="control-label"/>
        <form:input cssClass="form-control" path="lastModifiedBy" id="lastModifiedBy"  maxlength="100"/>
        <form:errors path="lastModifiedBy" cssClass="help-block"/>
    </div>
    <spring:bind path="taskDefinition.beanClass">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="taskDefinition.beanClass" styleClass="control-label"/>
        <form:input cssClass="form-control" path="beanClass" id="beanClass"  maxlength="1024"/>
        <form:errors path="beanClass" cssClass="help-block"/>
    </div>
    <spring:bind path="taskDefinition.beanName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="taskDefinition.beanName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="beanName" id="beanName"  maxlength="1024"/>
        <form:errors path="beanName" cssClass="help-block"/>
    </div>
    <spring:bind path="taskDefinition.cron">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="taskDefinition.cron" styleClass="control-label"/>
        <form:input cssClass="form-control" path="cron" id="cron"  maxlength="1024"/>
        <form:errors path="cron" cssClass="help-block"/>
    </div>
    <spring:bind path="taskDefinition.description">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="taskDefinition.description" styleClass="control-label"/>
        <form:input cssClass="form-control" path="description" id="description"  maxlength="1024"/>
        <form:errors path="description" cssClass="help-block"/>
    </div>
    <spring:bind path="taskDefinition.methodName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="taskDefinition.methodName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="methodName" id="methodName"  maxlength="1024"/>
        <form:errors path="methodName" cssClass="help-block"/>
    </div>
    <spring:bind path="taskDefinition.name">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="taskDefinition.name" styleClass="control-label"/>
        <form:input cssClass="form-control" path="name" id="name"  maxlength="100"/>
        <form:errors path="name" cssClass="help-block"/>
    </div>
    <spring:bind path="taskDefinition.start">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="taskDefinition.start" styleClass="control-label"/>
        <form:checkbox path="start" id="start" cssClass="checkbox"/>
        <form:errors path="start" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty taskDefinition.id}">
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

<v:javascript formName="taskDefinition" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['taskDefinitionForm']).focus();
    });
</script>
