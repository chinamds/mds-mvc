<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="cultureDetail.title"/></title>
    <meta name="menu" content="CultureMenu"/>
    <meta name="heading" content="<fmt:message key='cultureDetail.heading'/>"/>
</head>

<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/i18n/neutralresource.js"%>
</c:set>


<c:set var="delObject" scope="request"><fmt:message key="cultureList.culture"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="cultureDetail.heading"/></h2>
    <fmt:message key="cultureDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="culture" method="post" action="cultureform" cssClass="well"
           id="cultureForm" onsubmit="return validateCulture(this)">
<form:hidden path="id"/>
    <spring:bind path="culture.cultureCode">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="culture.cultureCode" styleClass="control-label"/>
        <form:input cssClass="form-control" path="cultureCode" id="cultureCode"  maxlength="256"/>
        <form:errors path="cultureCode" cssClass="help-block"/>
    </div>
    <spring:bind path="culture.cultureName">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="culture.cultureName" styleClass="control-label"/>
        <form:input cssClass="form-control" path="cultureName" id="cultureName"  maxlength="256"/>
        <form:errors path="cultureName" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty culture.id}">
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

<v:javascript formName="culture" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['cultureForm']).focus();
    });
</script>
