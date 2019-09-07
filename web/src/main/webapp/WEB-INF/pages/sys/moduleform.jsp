<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="moduleDetail.title"/></title>
    <meta name="menu" content="ModuleMenu"/>
    <meta name="heading" content="<fmt:message key='moduleDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="moduleList.module"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="moduleDetail.heading"/></h2>
    <fmt:message key="moduleDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="module" method="post" action="moduleform" cssClass="well"
           id="moduleForm" onsubmit="return validateModule(this)">
<form:hidden path="id"/>
    <spring:bind path="module.NDescLangId">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="module.NDescLangId" styleClass="control-label"/>
        <form:input cssClass="form-control" path="NDescLangId" id="NDescLangId"  maxlength="255"/>
        <form:errors path="NDescLangId" cssClass="help-block"/>
    </div>
    <spring:bind path="module.NTitleLangId">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="module.NTitleLangId" styleClass="control-label"/>
        <form:input cssClass="form-control" path="NTitleLangId" id="NTitleLangId"  maxlength="255"/>
        <form:errors path="NTitleLangId" cssClass="help-block"/>
    </div>
    <spring:bind path="module.moduleFlag">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="module.moduleFlag" styleClass="control-label"/>
        <form:input cssClass="form-control" path="moduleFlag" id="moduleFlag"  maxlength="255"/>
        <form:errors path="moduleFlag" cssClass="help-block"/>
    </div>
    <spring:bind path="module.moduleGroup">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="module.moduleGroup" styleClass="control-label"/>
        <form:input cssClass="form-control" path="moduleGroup" id="moduleGroup"  maxlength="50"/>
        <form:errors path="moduleGroup" cssClass="help-block"/>
    </div>
    <spring:bind path="module.moduleIcon">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="module.moduleIcon" styleClass="control-label"/>
        <form:input cssClass="form-control" path="moduleIcon" id="moduleIcon"  maxlength="255"/>
        <form:errors path="moduleIcon" cssClass="help-block"/>
    </div>
    <spring:bind path="module.moduleName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="module.moduleName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="moduleName" id="moduleName"  maxlength="50"/>
        <form:errors path="moduleName" cssClass="help-block"/>
    </div>
    <spring:bind path="module.moduleType">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="module.moduleType" styleClass="control-label"/>
        <form:input cssClass="form-control" path="moduleType" id="moduleType"  maxlength="255"/>
        <form:errors path="moduleType" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty module.id}">
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

<v:javascript formName="module" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['moduleForm']).focus();
    });
</script>
