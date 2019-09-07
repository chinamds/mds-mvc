<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.mds.core.ApprovalStatus" %>

<head>
    <title><fmt:message key="contentListDetail.title"/></title>
    <meta name="menu" content="ContentListMenu"/>
    <meta name="heading" content="<fmt:message key='contentListDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_appendgrid" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/cm/contentListform.js"%>
</c:set>

<c:set var="delObject" scope="request"><fmt:message key="contentListDetail.contentList"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="contentListDetail.heading"/></h2>
    <fmt:message key="contentListDetail.message"/>
</div>

<div class="onoffcanvas is-fixed is-center p-3 bg-white rounded" id="onoffcanvas-dialog">
    <div id="dcm_dl" class='dcm_ns'>
	    <a href="#onoffcanvas-dialog" class="onoffcanvas-toggler pull-right bg-danger" data-toggle="onoffcanvas"></a>
		<hr>
    	<div id="dcm_dl_mediaHtml"></div>
    </div>
</div>

<%-- <div id="importContentBox" class="hidden d-none">
       <cm:albumtreepicker id="album" keyName="albumId" keyValue="" fieldName="albumName" fieldValue=""
		cssClass="required form-control" maxlength="100" allowMultiCheck="false" treeViewTheme="default" requiredSecurityPermissions="${requiredSecurityPermissions}" galleryId="${galleryId}"
	    enableCheckboxPlugin="false" requireAlbumSelection="true" selectedAlbumIds="${selectedAlbumIds}"/>
</div> --%>

<div class="col-sm-9">
	<div id="rootwizard">
		<ul class="hidden d-none">
		  	<li><a href="#tab1" data-toggle="tab"><fmt:message key="contentList.step.first"/></a></li>
			<li><a href="#tab2" data-toggle="tab"><fmt:message key="contentList.step.second"/></a></li>
		</ul>
		<div class="tab-content">
		    <div class="tab-pane" id="tab1">
				<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
				<form:form modelAttribute="contentList" method="post" action="contentListform" cssClass="well"
				           id="contentListForm" onsubmit="return validateContentList(this)">
				           
					<input type="hidden" id="method" name="method" value="<c:out value="${param.method}"/>"/>
					<input type="hidden" id="jsonItems" name="jsonItems" value="${jsonItems}"/>
					<input type="hidden" id="currentStep" name="currentStep" value="0"/>
					<form:hidden path="id"/>
					<form:hidden path="createdBy"/>
					<form:hidden path="dateAdded"/>
					<form:hidden path="lastModifiedBy"/>
					<form:hidden path="dateLastModified"/>
					
					<div class="row">
						<spring:bind path="contentList.contentName">
						<div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
						</spring:bind>
					        <appfuse:label key="contentList.contentName" styleClass="control-label"/>
				        	<form:input cssClass="form-control" path="contentName" id="contentName"  maxlength="50"/>
					        <form:errors path="contentName" cssClass="help-block"/>
					    </div>
						
						<spring:bind path="contentList.desc">
					    <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
					    </spring:bind>
					        <appfuse:label key="contentList.desc" styleClass="control-label"/>
				        	<form:textarea path="desc" id="desc" row='2' cssClass="form-control"/>
					        <form:errors path="desc" cssClass="help-block"/>
					    </div>
				    </div>
				    
				    <spring:bind path="contentList.organization">
				    <div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
				    </spring:bind>
						<appfuse:label key="contentList.organization" styleClass="control-label"/>
						<input type="hidden" name="organizationId" id="organizationId" value="${contentList.organization.id}"/>
						<comm:ztreepicker id="organization" keyName="organizationId" keyValue="${contentList.organization.id}" fieldName="organizationName" fieldValue="${contentList.organization.fullName}"
							url="/sys/organizations/treeData" extId="${contentList.organization.id}" cssClass="required form-control" maxlength="200"/>
						<form:errors path="organization" cssClass="help-block"/>
					</div>
					
					<div class="form-group">
					    <div class="dcm_floatcontainer">
						    <div class="table-responsive">
						    	<table id="tblAppendGrid" class="table table-condensed" style="margin-top: 10px;"></table>
						    </div>
					    </div>
				    	
				    	<%-- <p class="toolbar">
				            <button id="btnContentImport" class="btn btn-success" type="button" title="<fmt:message key="button.import"/>">
					        	<i class="fa fa-file-import"></i> <fmt:message key="button.import"/></button>
					        <button class="btn btn-danger" id="delete" name="delete">
						        <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
				        </p>
						<table id="table2" data-toolbar=".toolbar">
					        <thead>
					        <tr>
					            <th data-field="state" data-checkbox="true"></th>
					            <th data-field="id"
					                data-align="center"
					                data-formatter="actionFormatter"
					                data-events="actionEvents"><fmt:message key="table.operation"/></th>
					            <th data-field="content" data-editable="true"><fmt:message key="contentListZone.content"/></th>
						        <th data-field="fileName"><fmt:message key="contentListZone.fileName"/></th>       
						        <th data-field="timeFrom" data-editable="true"><fmt:message key="contentListZone.timeFrom"/></th>        
						        <th data-field="timeTo" data-editable="true"><fmt:message key="contentListZone.timeTo"/></th>
					        </tr>
					        </thead>
					    </table> --%>
				    </div>
					    
				    <div class="form-group">	    
				    	<secure:hasAnyPermissions name="cm:contentLists:add,cm:contentLists:edit">
				        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
				            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
				        </button>
				        </secure:hasAnyPermissions>
				        <secure:hasPermission name="cm:contentLists:delete">
				        <c:if test="${not empty contentList.id}">
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
			<div class="tab-pane" id="tab2">
<%-- 		        <cm:albumtreepicker id="album" keyName="albumId" keyValue="" fieldName="albumName" fieldValue=""
					cssClass="required form-control" maxlength="100" allowMultiCheck="false" treeViewTheme="default" requiredSecurityPermissions="${requiredSecurityPermissions}" galleryId="${galleryId}"
				    enableCheckboxPlugin="false" requireAlbumSelection="true" selectedAlbumIds="${selectedAlbumIds}"/> --%>
			</div>
		</div>
	</div>
</div>

<v:javascript formName="contentList" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<!-- <script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['contentListForm']).focus();
    });
</script> -->
