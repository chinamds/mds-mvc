<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="appSettingDetail.title"/></title>
    <meta name="menu" content="AppSettingMenu"/>
    <meta name="heading" content="<fmt:message key='appSettingDetail.heading'/>"/>
</head>

<c:set var="delObject" scope="request"><fmt:message key="appSettingList.appSetting"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="appSettingDetail.heading"/></h2>
    <fmt:message key="appSettingDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="appSetting" method="post" action="appSettingform" cssClass="well"
           id="appSettingForm" onsubmit="return validateAppSetting(this)">
<form:hidden path="id"/>
    <spring:bind path="appSetting.settingName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="appSetting.settingName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="settingName" id="settingName"  maxlength="200"/>
        <form:errors path="settingName" cssClass="help-block"/>
    </div>
    <spring:bind path="appSetting.settingValue">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="appSetting.settingValue" styleClass="control-label"/>
        <form:input cssClass="form-control" path="settingValue" id="settingValue"  maxlength="255"/>
        <form:errors path="settingValue" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty appSetting.id}">
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

<v:javascript formName="appSetting" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['appSettingForm']).focus();
    });
</script>
