<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="activityDetail.title"/></title>
    <meta name="menu" content="ActivityMenu"/>
    <meta name="heading" content="<fmt:message key='activityDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_bootstrap-tabs-x" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/wf/activityform.js"%>
</c:set>

<c:set var="delObject" scope="request"><fmt:message key="activityDetail.activity"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="activityDetail.heading"/></h2>
    <fmt:message key="activityDetail.message"/>
</div>

<div class="col-sm-6">
	<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
	<form:form modelAttribute="activity" method="post" action="activityform" cssClass="well"
	           id="activityForm" onsubmit="return validateActivity(this)">
	           
		<input type="hidden" id="method" name="method" value="<c:out value="${param.method}"/>"/>
		<input type="hidden" id="currentStep" name="currentStep" value="0"/>
		<form:hidden path="id"/>
		<form:hidden path="createdBy"/>
		<form:hidden path="dateAdded"/>
		<form:hidden path="lastModifiedBy"/>
		<form:hidden path="dateLastModified"/>
		
		<spring:bind path="activity.code">
		<div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
		</spring:bind>
	        <appfuse:label key="activity.code" styleClass="control-label"/>
        	<form:input cssClass="form-control" path="code" id="code"  maxlength="50"/>
	        <form:errors path="code" cssClass="help-block"/>
	    </div>
	    
	    <spring:bind path="activity.organization">
	    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
	    </spring:bind>
			<appfuse:label key="activity.organization" styleClass="control-label"/>
			<input type="hidden" name="organizationId" id="organizationId" value="${activity.organization.id}"/>
			<comm:ztreepicker id="organization" keyName="organizationId" keyValue="${activity.organization.id}" fieldName="organizationName" fieldValue="${activity.organization.fullName}"
				url="/sys/organizations/treeData" extId="${activity.organization.id}" cssClass="required form-control" maxlength="200" selectChanged="selectChanged"/>
			<form:errors path="organization" cssClass="help-block"/>
		</div>
			
	    <spring:bind path="activity.description">
	    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
	    </spring:bind>
	        <appfuse:label key="activity.description" styleClass="control-label"/>
        	<form:textarea path="description" id="description" row='2' cssClass="form-control"/>
	        <form:errors path="description" cssClass="help-block"/>
	    </div>
	    
	    <div class="form-group">
		    <div class="tabs-x tabs-above">
	            <ul id="myTab-kv-1" class="nav nav-tabs" role="tablist">
	                <li class="active"><a href="#organizations-activity-1" role="tab" data-bs-toggle="tab"><i
	                        class="fa fa-sitemap"></i> <fmt:message key="activity.organizations"/></a></li>
	                <li><a href="#users-activity-1" role="tab-kv" data-bs-toggle="tab"><i class="fa fa-users"></i>
	                    <fmt:message key="activity.users"/></a></li>
	            </ul>
	            <div id="myTabContent-kv-1" class="tab-content">
	                <div class="tab-pane fade in active" id="organizations-activity-1">
	                    <div id="organizationsTree" >loading... ...</div>
	                </div>
	                <div class="tab-pane fade" id="users-activity-1">
					    <select multiple="multiple" size="10" name="users" class="form-control" id="users">
					    	<c:forEach items="${users}" var="user">
					            <option value="${user.id}" ${fn:contains(selectedUsers, user) ? 'selected' : ''}>${user.username}</option>
					        </c:forEach>
				        </select>
	                </div>
	            </div>
	        </div>
         </div>
            
		    
	    <div class="form-group">
	    	<input type="hidden" id="organizationIds" name="organizationIds" />
	    	<secure:hasAnyPermissions name="wf:activities:add,wf:activities:edit">
	        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
	            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
	        </button>
	        </secure:hasAnyPermissions>
	        <secure:hasPermission name="wf:activities:delete">
	        <c:if test="${not empty activity.id}">
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

<v:javascript formName="activity" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<!-- <script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['activityForm']).focus();
    });
</script> -->
