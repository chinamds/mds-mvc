<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="roleDetail.title"/></title>
    <meta name="menu" content="RoleMenu"/>
    <meta name="heading" content="<fmt:message key='roleDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/sys/roleAccessGalleryform.js"%>
</c:set>

<div class="col-sm-3">
    <h2><fmt:message key="roleDetail.heading"/></h2>
    <fmt:message key="roleDetail.message"/>
</div>

<div class="col-sm-6">
<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
<form:form modelAttribute="role" method="post" action="roleAccessGalleryform" cssClass="well"
           id="roleAccessGalleryForm" onsubmit="return validateRole(this)">
<form:hidden path="id"/>
<form:hidden path="type"/>
<%-- <c:if test="${not empty role.organization}"> --%>
<form:hidden path="organization"/>
<%-- </c:if> --%>
<form:hidden path="description"/>
<form:hidden path="createdBy"/>
<form:hidden path="dateAdded"/>
<form:hidden path="lastModifiedBy"/>
<form:hidden path="dateLastModified"/>
    <spring:bind path="role.name">
    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
    </spring:bind>
        <appfuse:label key="role.name" styleClass="control-label"/>
        <form:input cssClass="form-control" path="name" id="name"  readonly="true" maxlength="100"/>
        <form:errors path="name" cssClass="help-block"/>
    </div>
    <spring:bind path="role.galleries">
    <div class="form-group">
    </spring:bind>
    	<appfuse:label key="role.galleries" styleClass="control-label"/>
	    <select ${role.type == RoleType.ga ? '' : 'multiple="multiple"'}  size="10" name="galleries" class="form-control" id="galleries">
	    	<c:forEach items="${galleries}" var="gallery">
	            <option value="${gallery.id}" ${fn:contains(role.galleries, gallery) ? 'selected' : ''}>${gallery.name}</option>
	        </c:forEach>
        </select>
        <form:errors path="galleries" cssClass="help-block"/>
   </div>
    <div class="form-group">
    	<input type="hidden" id="galleryIds" name="galleryIds" />
    	<secure:hasAnyPermissions name="sys:roles:add,sys:roles:edit">
        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false;">
            <i class="fa fa-ok icon-white"></i> <fmt:message key="button.save"/>
        </button>
        </secure:hasAnyPermissions>
        
        <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
            <i class="fa fa-remove"></i> <fmt:message key="button.cancel"/>
        </button>
    </div>
</form:form>
</div>

<v:javascript formName="role" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>
