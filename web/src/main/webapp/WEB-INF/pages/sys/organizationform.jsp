<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="organizationDetail.title"/></title>
    <meta name="menu" content="OrganizationMenu"/>
    <meta name="heading" content="<fmt:message key='organizationDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/sys/organizationform.js"%>
</c:set>

<c:set var="delObject" scope="request"><fmt:message key="organizationList.organization"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="organizationDetail.heading"/></h2>
    <fmt:message key="organizationDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="organization" method="post" action="organizationform" cssClass="well"
           id="organizationForm" onsubmit="return validateForm(this)" enctype="multipart/form-data">
<form:hidden path="id"/>
<form:hidden path="createdBy"/>
<form:hidden path="dateAdded"/>
<form:hidden path="lastModifiedBy"/>
<form:hidden path="dateLastModified"/>
    <spring:bind path="organization.code">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="organization.code" styleClass="control-label"/>
        <form:input cssClass="form-control" path="code" id="code"  maxlength="100"/>
        <form:errors path="code" cssClass="help-block"/>
    </div>
    <spring:bind path="organization.name">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="organization.name" styleClass="control-label"/>
        <form:input cssClass="form-control" path="name" id="name"  maxlength="256"/>
        <form:errors path="name" cssClass="help-block"/>
    </div>
    <!-- todo: change this to read the identifier field from the other pojo -->
   <%--  <form:select cssClass="form-control" path="parent" items="parentList" itemLabel="label" itemValue="value"/> --%>
   <secure:hasPermission name="sys:organizations:addchild">
   <div class="form-group">
		<appfuse:label key="organization.parent" styleClass="control-label"/>
		<form:hidden path="parent.id" id="parentId"/>
		<comm:ztreepicker id="parent" keyName="parentId" keyValue="${organization.parent.id}" fieldName="parentName" fieldValue="${organization.parent.fullName}"
			 url="/sys/organizations/treeData" extId="${organization.id}" cssClass="required form-control" maxlength="20"/>
	</div>
	</secure:hasPermission>
	<secure:lacksPermission name="sys:organizations:addchild">
		<form:hidden path="parent.id"/>
	</secure:lacksPermission>	
	<spring:bind path="organization.area.id">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
		<appfuse:label key="organization.area" styleClass="control-label"/>
		<form:hidden path="area.id" id="areaId"/>
		<comm:ztreepicker id="area" keyName="areaId" keyValue="${organization.area.id}" fieldName="areaName" fieldValue="${organization.area.fullName}"
			url="/sys/areas/treeData" extId="${organization.area.id}" cssClass="required form-control" maxlength="20"/>
		<form:errors path="area.id" cssClass="help-block"/>
	</div>
	<spring:bind path="organization.available">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="organization.available" styleClass="control-label"/>
        <form:checkbox path="available" id="available" cssClass="checkbox"/>
        <form:errors path="available" cssClass="help-block"/>
    </div>
    <spring:bind path="organization.header">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="organization.header" styleClass="control-label"/>
        <form:input cssClass="form-control" path="header" id="header"  maxlength="255"/>
        <form:errors path="header" cssClass="help-block"/>
    </div>
    <spring:bind path="organization.phone">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="organization.phone" styleClass="control-label"/>
        <form:input cssClass="form-control" path="phone" id="phone"  maxlength="255"/>
        <form:errors path="phone" cssClass="help-block"/>
    </div>
    <spring:bind path="organization.fax">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="organization.fax" styleClass="control-label"/>
        <form:input cssClass="form-control" path="fax" id="fax"  maxlength="255"/>
        <form:errors path="fax" cssClass="help-block"/>
    </div>
    <spring:bind path="organization.email">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="organization.email" styleClass="control-label"/>
        <form:input cssClass="form-control" path="email" id="email"  maxlength="255"/>
        <form:errors path="email" cssClass="help-block"/>
    </div>
    <spring:bind path="organization.webSite">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="organization.webSite" styleClass="control-label"/>
        <form:input cssClass="form-control" path="webSite" id="webSite"  maxlength="255"/>
        <form:errors path="webSite" cssClass="help-block"/>
    </div>
    <spring:bind path="organization.preferredlanguage">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="organization.preferredlanguage" styleClass="control-label"/>
        <select class="form-control" id="preferredlanguage" name="preferredlanguage">
	    	<option selected="selected" value="${organization.preferredlanguage.cultureCode}">${organization.preferredlanguage.cultureName}</option>
		</select>
        <form:errors path="preferredlanguage" cssClass="help-block"/>
    </div>
    <spring:bind path="organization.logo">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="organization.logo" styleClass="control-label"/>
        <input type="hidden" id="removelogo" name="removelogo" value="false" />
        <!-- <input id="logofile" name="logofile" type="file"> -->
        <div class="text-center">
            <div class="kv-avatar">
                <div class="file-loading">
                    <input id="logofile" name="logofile" type="file">
                </div>
            </div>
            <div class="kv-avatar-hint"><small>Select file < 1500 KB</small></div>
        </div>
        <form:errors path="logo" cssClass="help-block"/>
    </div>
    <spring:bind path="organization.description">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="organization.description" styleClass="control-label"/>
        <form:textarea path="description" id="description" row='3' cssClass="form-control"/>
        <form:errors path="description" cssClass="help-block"/>
    </div>
    <div>
        <legend class="accordion-heading">
            <a data-toggle="collapse" href="#collapse-address"><fmt:message key="organization.address.address"/></a>
        </legend>
        <div id="collapse-address" class="accordion-body collapse">
            <div class="form-group">
                <appfuse:label styleClass="control-label" key="organization.address.address"/>
                <form:input cssClass="form-control" path="address.address" id="address.address"/>
            </div>
            <div class="row">
                <div class="col-sm-7 form-group">
                    <appfuse:label styleClass="control-label" key="organization.address.city"/>
                    <form:input cssClass="form-control" path="address.city" id="address.city"/>
                </div>
                <div class="col-sm-2 form-group">
                    <appfuse:label styleClass="control-label" key="organization.address.province"/>
                    <form:input cssClass="form-control" path="address.province" id="address.province"/>
                </div>
                <div class="col-sm-3 form-group">
                    <appfuse:label styleClass="control-label" key="organization.address.postalCode"/>
                    <form:input cssClass="form-control" path="address.postalCode" id="address.postalCode"/>
                </div>
            </div>
            <div class="form-group">
                <appfuse:label styleClass="control-label" key="organization.address.country"/>
                <form:input cssClass="form-control" path="address.country" id="address.country"/>
                <%-- <appfuse:country name="address.country" prompt="" default="${organization.address.country}"/> --%>
            </div>
        </div>
    </div>
    <div class="form-group">
    	<secure:hasAnyPermissions name="sys:organizations:add,sys:organizations:edit">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        </secure:hasAnyPermissions>
        <secure:hasPermission name="sys:organizations:delete">
        <c:if test="${not empty organization.id}">
            <button type="submit" class="btn btn-danger" id="delete" name="delete" onclick="bCancel=true;return confirmMessage(msgDelConfirm)">
                <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/>
            </button>
        </c:if>
        </secure:hasPermission>

        <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
        </button>
    </div>
</form:form>
</div>

<v:javascript formName="organization" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['organizationForm']).focus();
    });
</script>
