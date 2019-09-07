<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="workflowDetail.title"/></title>
    <meta name="menu" content="WorkflowMenu"/>
    <meta name="heading" content="<fmt:message key='workflowDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_appendgrid" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/wf/workflowform.js"%>
</c:set>

<c:set var="delObject" scope="request"><fmt:message key="workflowDetail.workflow"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="workflowDetail.heading"/></h2>
    <fmt:message key="workflowDetail.message"/>
</div>

<div class="col-sm-9">
	<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
	<form:form modelAttribute="workflow" method="post" action="workflowform" cssClass="well"
	           id="workflowForm" onsubmit="return validateWorkflow(this)">
	           
		<input type="hidden" id="method" name="method" value="${method}"/>
		<form:hidden path="id"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="dateAdded"/>
		<form:hidden path="lastModifiedBy"/>
		<form:hidden path="dateLastModified"/>
		
		<spring:bind path="workflow.workflowName">
		<div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
		</spring:bind>
	        <appfuse:label key="workflow.workflowName" styleClass="control-label"/>
        	<form:input cssClass="form-control" path="workflowName" id="workflowName"  maxlength="50"/>
	        <form:errors path="workflowName" cssClass="help-block"/>
	    </div>
					    
    	<spring:bind path="workflow.workflowType">
	    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
	    </spring:bind>
	        <appfuse:label key="workflow.workflowType" styleClass="control-label"/>
	        <select class="form-control" id="workflowType" name="workflowType">
		    	<option selected="selected" value="${workflow.workflowType.id}">${workflow.workflowType.workflowType}</option>
			</select>
	        <form:errors path="workflowType" cssClass="help-block"/>
	    </div>
	    
	    <%-- <spring:bind path="workflow.desc">
	    <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
	    </spring:bind>
	        <appfuse:label key="workflow.desc" styleClass="control-label"/>
        	<form:textarea path="desc" id="desc" row='2' cssClass="form-control"/>
	        <form:errors path="desc" cssClass="help-block"/>
	    </div> --%>
		
		<div class="form-group">
		    <div class="dcm_floatcontainer">
			    <div class="table-responsive">
			    	<table id="tblAppendGrid" class="table table-condensed" style="margin-top: 10px;"></table>
			    </div>
		    </div>
	    </div>
		    
	    <div class="form-group">
	    	<secure:hasAnyPermissions name="wf:workflows:add,wf:workflows:edit">
	        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
	            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
	        </button>
	        </secure:hasAnyPermissions>
	        <secure:hasPermission name="wf:workflows:delete">
	        <c:if test="${not empty workflow.id}">
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

<v:javascript formName="workflow" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<!-- <script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['workflowForm']).focus();
    });
</script> -->
