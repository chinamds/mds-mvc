<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.mds.core.ApprovalStatus" %>

<head>
    <title><fmt:message key="dailyListDetail.title"/></title>
    <meta name="menu" content="DailyListMenu"/>
    <meta name="heading" content="<fmt:message key='dailyListDetail.heading'/>"/>
</head>

<c:set var="group" value="grp_appendgrid" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/cm/dailyListform.js"%>
</c:set>

<c:set var="delObject" scope="request"><fmt:message key="dailyListDetail.dailyList"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<div class="col-sm-3">
    <h2><fmt:message key="dailyListDetail.heading"/></h2>
    <fmt:message key="dailyListDetail.message"/>
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
		  	<li><a href="#tab1" data-toggle="tab"><fmt:message key="dailyList.step.first"/></a></li>
			<li><a href="#tab2" data-toggle="tab"><fmt:message key="dailyList.step.second"/></a></li>
		</ul>
		<div class="tab-content">
		    <div class="tab-pane" id="tab1">
				<form:errors path="*" cssClass="alert alert-danger alert-dismissable" element="div"/>
				<form:form modelAttribute="dailyList" method="post" action="dailyListform" cssClass="well"
				           id="dailyListForm" onsubmit="return validateDailyList(this)">
				           
					<input type="hidden" id="method" name="method" value="<c:out value="${param.method}"/>"/>
					<input type="hidden" id="jsonItems" name="jsonItems" value="${jsonItems}"/>
					<input type="hidden" id="independentSpaceForDailyList" name="independentSpaceForDailyList" value="${independentSpaceForDailyList}"/>
					<input type="hidden" id="currentStep" name="currentStep" value="0"/>
					<form:hidden path="id"/>
					<form:hidden path="createdBy"/>
					<form:hidden path="dateAdded"/>
					<form:hidden path="lastModifiedBy"/>
					<form:hidden path="dateLastModified"/>
					
					<div class="row">
						<spring:bind path="dailyList.contentName">
						<div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
						</spring:bind>
<c:if test="${independentSpaceForDailyList}">					
							<appfuse:label key="dailyList.contentName" styleClass="control-label"/>
</c:if>
<c:if test="${not independentSpaceForDailyList}">
							<appfuse:label key="dailyList.gallery" styleClass="control-label"/>
</c:if>												
				        	<form:input cssClass="form-control" path="contentName" id="contentName"  maxlength="50"/>
					        <form:errors path="contentName" cssClass="help-block"/>
					    </div>
						
						<spring:bind path="dailyList.date">
					    <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
					    </spring:bind>
					        <appfuse:label key="dailyList.date" styleClass="control-label"/>
					        <div class="input-group date" id="datetimepicker1" data-target-input="nearest">
					            <form:input path="date" cssClass="form-control datetimepicker-input" data-target="#datetimepicker1" data-position="bottom-left" data-format="${fn:toUpperCase(datePattern)}"/>
					            <div class="input-group-append input-group-addon" data-target="#datetimepicker1" data-toggle="datetimepicker">
			                        <div class="input-group-text"><i class="fa fa-calendar icon-calendar"></i></div>
			                    </div>
					        </div>
					        
					        <form:errors path="date" cssClass="help-block"/>
					    </div>
				    </div>

<%-- <c:if test="${param.method == 'Add'}">	 --%>			    
				    <div class="row">
<c:if test="${independentSpaceForDailyList}">				    
<c:if test="${param.method == 'Edit' or param.method == 'Approve'}">
						<input type="hidden" id="galleryId" name="galleryId" value="${dailyList.gallery.id}"/>
</c:if>				    
				    	<spring:bind path="dailyList.gallery">
					    <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
					    </spring:bind>
					        <appfuse:label key="dailyList.gallery" styleClass="control-label"/>
					        <select class="form-control" id="gallery" name="gallery">
						    	<option selected="selected" value="${dailyList.gallery.id}">${dailyList.gallery.name}</option>
							</select>
					        <form:errors path="gallery" cssClass="help-block"/>
					    </div>
</c:if>
<c:if test="${not independentSpaceForDailyList}">
						<spring:bind path="dailyList.organization">
					    <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
					    </spring:bind>
							<appfuse:label key="dailyList.organization" styleClass="control-label"/>
							<input type="hidden" name="organizationId" id="organizationId" value="${dailyList.organization.id}"/>
							<comm:ztreepicker id="organization" keyName="organizationId" keyValue="${dailyList.organization.id}" fieldName="organizationName" fieldValue="${dailyList.organization.fullName}"
								url="/sys/organizations/treeData" extId="${dailyList.organization.id}" cssClass="required form-control" maxlength="200"/>
							<form:errors path="organization" cssClass="help-block"/>
						</div>
</c:if>
					    <spring:bind path="dailyList.desc">
					    <div class="col-sm-6 form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
					    </spring:bind>
					        <appfuse:label key="dailyList.desc" styleClass="control-label"/>
				        	<form:textarea path="desc" id="desc" row='2' cssClass="form-control"/>
					        <form:errors path="desc" cssClass="help-block"/>
					    </div>
				    </div>
<c:if test="${param.method == 'Approve'}">				    
				    <div class="form-group">
			            <label class="col-sm-3 control-label"><fmt:message key="dailyList.approvalOpinion"/></label>
			            <div class="col-sm-9">
			            	<textarea name="approvalOpinion" id="approvalOpinion" class="form-control" title="<fmt:message key="dailyList.approvalOpinion"/>" rows='2'></textarea>
			            </div>
			        </div>
</c:if>			        
<%-- </c:if>
<c:if test="${param.method == 'Edit'}">
					<input type="hidden" id="galleryId" name="galleryId" value="${dailyList.gallery.id}"/>
					<spring:bind path="dailyList.desc">
					<div class="form-group${(not empty status.errorMessage) ? ' has-error' : ''}">
					</spring:bind>
				        <appfuse:label key="dailyList.desc" styleClass="control-label"/>
			        	<form:textarea path="desc" id="desc" row='2' cssClass="form-control"/>
				        <form:errors path="desc" cssClass="help-block"/>
				    </div>
</c:if>	 --%>			    
								        
				    <%-- <div class="form-group">
					    <button id="btnContentImport" class="btn btn-success" type="button" title="<fmt:message key="button.import"/>">
					        <i class="fa fa-file-import"></i> <fmt:message key="button.import"/></button>
				        <button class="btn btn-danger" id="delete" name="delete">
					        <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
					</div>    --%>
					
					<div class="form-group">
					    <div class="mds_floatcontainer">
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
					            <th data-field="content" data-editable="true"><fmt:message key="dailyListZone.content"/></th>
						        <th data-field="fileName"><fmt:message key="dailyListZone.fileName"/></th>       
						        <th data-field="timeFrom" data-editable="true"><fmt:message key="dailyListZone.timeFrom"/></th>        
						        <th data-field="timeTo" data-editable="true"><fmt:message key="dailyListZone.timeTo"/></th>
					        </tr>
					        </thead>
					    </table> --%>
				    </div>
					    
				    <div class="form-group">
<c:if test="${param.method != 'Approve'}">
<c:if test="${dailyList.approvalStatus != ApprovalStatus.Approving and dailyList.approvalStatus != ApprovalStatus.Approved }">		    
				    	<secure:hasAnyPermissions name="cm:dailyLists:add,cm:dailyLists:edit">
				        <button type="submit" class="btn btn-primary" id="save" name="save" onclick="bCancel=false">
				            <i class="fa fa-check icon-white"></i> <fmt:message key="button.save"/>
				        </button>
				        </secure:hasAnyPermissions>
				        <secure:hasPermission name="cm:dailyLists:delete">
				        <c:if test="${not empty dailyList.id}">
				            <button type="submit" class="btn btn-danger" id="delete" name="delete" onclick="bCancel=true;return confirmMessage(msgDelConfirm)">
				                <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/>
				            </button>
				        </c:if>
				        </secure:hasPermission>
</c:if>				        
</c:if>
<c:if test="${param.method == 'Approve'}">
						<secure:hasPermission name="cm:dailyListsApproval:approve">
				        <button type="submit" class="btn btn-primary" id="approve" name="approve" onclick="bCancel=false">
				            <i class="fa fa-thumbs-up icon-white"></i> <fmt:message key="button.approve"/>
				        </button>
			            <button type="submit" class="btn btn-danger" id="reject" name="reject" onclick="bCancel=true;return confirmMessage(msgDelConfirm)">
			                <i class="fa fa-thumbs-down icon-white"></i> <fmt:message key="button.reject"/>
			            </button>
				        </secure:hasPermission>
</c:if>
				
				        <button type="submit" class="btn btn-default" id="cancel" name="cancel" onclick="bCancel=true">
				            <i class="fa fa-times"></i> <fmt:message key="button.cancel"/>
				        </button>
				    </div>
				</form:form>
			</div>
			<div class="tab-pane" id="tab2" style="margin-top: 10px;">
				<div class="form-group">
					<iframe id="treepickerFrame" name="treepickerFrame" width="100%" height="100%"  frameborder="0" scrolling="auto"
	             		src="${ctx}/cm/albumtreepickers" style="min-height:640px;"></iframe>
             	</div>
<%-- 		        <cm:albumtreepicker id="album" keyName="albumId" keyValue="" fieldName="albumName" fieldValue=""
					cssClass="required form-control" maxlength="100" allowMultiCheck="false" treeViewTheme="default" requiredSecurityPermissions="${requiredSecurityPermissions}" galleryId="${galleryId}"
				    enableCheckboxPlugin="false" requireAlbumSelection="true" selectedAlbumIds="${selectedAlbumIds}"/> --%>
			</div>
		</div>
	</div>
</div>

<v:javascript formName="dailyList" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>

<!-- <script type="text/javascript">
    $(document).ready(function() {
        $("input[type='text']:visible:enabled:first", document.forms['dailyListForm']).focus();
    });
</script> -->
