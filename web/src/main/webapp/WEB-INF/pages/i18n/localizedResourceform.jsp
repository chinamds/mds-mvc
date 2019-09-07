<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="localizedResourceDetail.title"/></title>
    <meta name="menu" content="LocalizedResourceMenu"/>
    <meta name="heading" content="<fmt:message key='localizedResourceDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/i18n/localizedResourceform.js"%>
</c:set>

<c:set var="delObject" scope="request"><fmt:message key="localizedResourceList.localizedResource"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="localizedResourceDetail.heading"/></h2>
    <fmt:message key="localizedResourceDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="localizedResource" method="post" action="localizedResourceform" cssClass="well"
           id="localizedResourceForm" onsubmit="return validateLocalizedResource(this)">
<form:hidden path="id"/>
    <div class="form-group">
        <appfuse:label key="localizedResource.resourceCategory" styleClass="control-label"/>
        <select id="resourceCategory" name="resourceCategory" class="form-control">
            <c:forEach items="${resourceCategories}" var="resourceCategory">
	            <option value="${resourceCategory}"><fmt:message key="${resourceCategory.info}"/></option>
            </c:forEach>
        </select>
    </div>
    
    <spring:bind path="localizedResource.neutralResource">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="localizedResource.neutralResource" styleClass="control-label"/>
        <select class="form-control" id="neutralResource" name="neutralResource">
	    	<option selected="selected" value="${localizedResource.neutralResource.resourceKey}">${localizedResource.neutralResource.value}</option>
		</select>
        <form:errors path="neutralResource" cssClass="help-block"/>
    </div>
    <spring:bind path="localizedResource.culture">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="localizedResource.culture" styleClass="control-label"/>
        <select class="form-control" id="culture" name="culture">
	    	<option selected="selected" value="${localizedResource.culture.cultureCode}">${localizedResource.culture.cultureName}</option>
		</select>
        <form:errors path="culture" cssClass="help-block"/>
    </div>

    <spring:bind path="localizedResource.value">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="localizedResource.value" styleClass="control-label"/>
        <form:input cssClass="form-control" path="value" id="value"  maxlength="255"/>
        <form:errors path="value" cssClass="help-block"/>
    </div>

    <div class="form-group">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        <c:if test="${not empty localizedResource.id}">
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

<v:javascript formName="localizedResource" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['localizedResourceForm']).focus();
    });
</script>
