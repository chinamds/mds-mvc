<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.mds.sys.model.RoleType" %>

<head>
    <title><fmt:message key="galleryDetail.title"/></title>
    <meta name="menu" content="GalleryMenu"/>
    <meta name="heading" content="<fmt:message key='galleryDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_form" scope="request" />

<c:set var="delObject" scope="request"><fmt:message key="galleryList.gallery"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="galleryDetail.heading"/></h2>
    <fmt:message key="galleryDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="gallery" method="post" action="galleryform" cssClass="well"
           id="galleryForm" onsubmit="return validateGallery(this)">
<form:hidden path="id"/>
<form:hidden path="createdBy"/>
<form:hidden path="dateAdded"/>
<form:hidden path="lastModifiedBy"/>
<form:hidden path="dateLastModified"/>
	<spring:bind path="gallery.name">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="gallery.name" styleClass="control-label"/>
        <form:input cssClass="form-control" path="name" id="name"  maxlength="50"/>
        <form:errors path="name" cssClass="help-block"/>
    </div>
	<secure:hasAnyRoleTypes name='${RoleType.sa.toString()},${RoleType.ad.toString()}'>    
    <spring:bind path="gallery.isTemplate">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="gallery.isTemplate" styleClass="control-label"/>
        <form:checkbox path="isTemplate" id="isTemplate" cssClass="checkbox"/>
        <form:errors path="isTemplate" cssClass="help-block"/>
    </div>
    </secure:hasAnyRoleTypes>
    <secure:lacksRoleTypes name='${RoleType.sa.toString()},${RoleType.ad.toString()}'>
    <form:hidden path="isTemplate"/>
    </secure:lacksRoleTypes>
    <spring:bind path="gallery.description">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="gallery.description" styleClass="control-label"/>
        <%-- <form:input cssClass="form-control" path="description" id="description"  maxlength="1000"/> --%>
        <form:textarea path="description" id="description" row='3' cssClass="form-control"/>
        <form:errors path="description" cssClass="help-block"/>
    </div>
    <div class="form-group">
    	<label for="organization" class="control-label"><fmt:message key="gallery.organization"/></label>
		<input type="hidden" name="organizationId" id="organizationId" value="${organization.id}"/>
		<comm:ztreepicker id="organization" keyName="organizationId" keyValue="${organization.id}" fieldName="organizationName" fieldValue="${organization.fullName}"
			url="/sys/organizations/treeData" extId="${organization.id}" cssClass="required form-control" maxlength="20"/>
    </div>
            
    <div class="form-group">
    	<secure:hasAnyPermissions name="cm:galleries:add,cm:galleries:edit">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
        </button>
        </secure:hasAnyPermissions>
        <secure:hasPermission name="cm:galleries:delete">
        <c:if test="${not empty gallery.id}">
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

<v:javascript formName="gallery" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['galleryForm']).focus();
    });
</script>
