<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="organizationWorkflowTypeDetail.title"/></title>
    <meta name="menu" content="OrganizationWorkflowTypeMenu"/>
    <meta name="heading" content="<fmt:message key='organizationWorkflowTypeDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/wf/organizationWorkflowTypeform.js"%>
</c:set>

<c:set var="delObject" scope="request"><fmt:message key="organizationWorkflowTypeDetail.organizationWorkflowType"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="organizationWorkflowTypeDetail.heading"/></h2>
    <fmt:message key="organizationWorkflowTypeDetail.message"/>
</div>

<div class="col-sm-6">
	<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
	<form:form modelAttribute="organizationWorkflowType" method="post" action="organizationWorkflowTypeform" cssClass="well"
	           id="organizationWorkflowTypeForm" onsubmit="return validateOrganizationWorkflowType(this)">
	           
		<form:hidden path="id"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="dateAdded"/>
		<form:hidden path="lastModifiedBy"/>
		<form:hidden path="dateLastModified"/>
		
	    <spring:bind path="organizationWorkflowType.workflowType">
	    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
	    </spring:bind>
	        <appfuse:label key="organizationWorkflowType.workflowType" styleClass="control-label"/>
	        <select id="workflowType" name="workflowType" class="form-control">
	            <c:forEach items="${workflowTypes}" var="workflowType">
	            <option value="${workflowType.workflowType}" ${ workflowType.workflowType eq organizationWorkflowType.workflowType ? 'selected' : ''}>${workflowType.info}</option>
	            </c:forEach>
	        </select>
	        <form:errors path="workflowType" cssClass="help-block"/>
	    </div>
   
		<spring:bind path="organizationWorkflowType.organization">
	    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
	    </spring:bind>
			<appfuse:label key="organizationWorkflowType.organization" styleClass="control-label"/>
			<input type="hidden" name="organizationId" id="organizationId" value="${organizationWorkflowType.organization.id}"/>
			<comm:ztreepicker id="organization" keyName="organizationId" keyValue="${organizationWorkflowType.organization.id}" fieldName="organizationName" fieldValue="${organizationWorkflowType.organization.fullName}"
				url="/sys/organizations/treeData" extId="${organizationWorkflowType.organization.id}" cssClass="required form-control" maxlength="200"/>
			<form:errors path="organization" cssClass="help-block"/>
		</div>
		
	    <spring:bind path="organizationWorkflowType.description">
	    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
	    </spring:bind>
	        <appfuse:label key="organizationWorkflowType.description" styleClass="control-label"/>
        	<form:textarea path="description" id="description" row='2' cssClass="form-control"/>
	        <form:errors path="description" cssClass="help-block"/>
	    </div>
		    
	    <div class="form-group">
	    	<secure:hasAnyPermissions name="wf:organizationWorkflowTypes:add,wf:organizationWorkflowTypes:edit">
	        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
	            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
	        </button>
	        </secure:hasAnyPermissions>
	        <secure:hasPermission name="wf:organizationWorkflowTypes:delete">
	        <c:if test="${not empty organizationWorkflowType.id}">
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

<v:javascript formName="organizationWorkflowType" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<!-- <script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['organizationWorkflowTypeForm']).focus();
    });
</script> -->
