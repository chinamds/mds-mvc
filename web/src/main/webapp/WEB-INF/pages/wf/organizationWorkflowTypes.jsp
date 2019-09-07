<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="organizationWorkflowTypeList.title"/></title>
    <meta name="menu" content="OrganizationWorkflowTypeMenu"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/wf/organizationWorkflowType.js"%>
</c:set>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="organizationWorkflowTypeList.heading"/></h2>

<form id="searchForm" class="form-inline float-right">
<div id="search" class="input-group text-right">
	 <input type="text" size="20" name="q" id="query" value="${param.q}"
               placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
    <span class="input-group-btn">
		<button id="buttonSearch" class="btn btn-default btn-sm">
	        <i class="fa fa-search"></i> <fmt:message key="button.search"/>
		</button>
	</span>
</div>
</form>

<p><fmt:message key="organizationWorkflowTypeList.message"/></p>

<div id="importBox" class="hidden d-none">
	<form id="importForm" action="${ctx}/wf/organizationWorkflowTypes/import" method="post" enctype="multipart/form-data"
		style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('<fmt:message key="importform.importing"/>');"><br/>
		<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>
		<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="<fmt:message key="button.import"/>"/>
		<a href="${ctx}/wf/organizationWorkflowTypes/import/template"><fmt:message key="button.downloadtemplate"/></a>
	</form>
</div>

<div id="actions" class="btn-group">
	<secure:hasPermission name="wf:organizationWorkflowTypes:add">
    <a href='<c:url value="/wf/organizationWorkflowTypeform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    </secure:hasPermission>
    <secure:hasPermission name="wf:organizationWorkflowTypes:delete">
    <button class="btn btn-danger" id="delete" name="delete">
        <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
    </secure:hasPermission>
    <secure:hasPermission name="wf:organizationWorkflowTypes:data_import">
    <button id="btnImport" class="btn btn-success hidden-xs" type="button" title="<fmt:message key="button.import"/>">
        <i class="fa fa-file-import"></i> <span class="hidden-xs"><fmt:message key="button.import"/></span></button>
     </secure:hasPermission>
     <secure:hasPermission name="wf:organizationWorkflowTypes:data_export">
    <button id="btnExport" class="btn btn-info hidden-xs" type="button" title="<fmt:message key="button.export"/>">
        <i class="fa fa-file-export"></i><span class="hidden-xs"><fmt:message key="button.export"/></span></button>
    </secure:hasPermission>
    <secure:hasAnyPermissions name="wf:organizationWorkflowTypes:data_import,wf:organizationWorkflowTypes:data_export">
    <div class="btn-group visible-xs-block visible-sm-block">
	    <button type="button" class="btn btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
		    <fmt:message key="button.moreactions"/> <span class="caret"></span></button>
		<ul class="dropdown-menu">
		  <secure:hasPermission name="wf:organizationWorkflowTypes:data_import">	
		  <li><a class="btn-moreactions-import"><i class="fa fa-file-import"></i>&nbsp;<fmt:message key="button.import"/></a></li>
		  </secure:hasPermission>
		  <secure:hasPermission name="wf:organizationWorkflowTypes:data_export">
		  <li><a class="btn-moreactions-export"><i class="fa fa-file-export"></i>&nbsp;<fmt:message key="button.export"/></a></li>
		  </secure:hasPermission>
		</ul>
	</div>
	</secure:hasAnyPermissions>
    <a href='<c:url value="/home"/>' class="btn btn-default visible-xs-block visible-sm-block d-md-none d-xs-block d-sm-block"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>   
<div class="table-responsive">
	<table id="table">
       <thead>
       <tr>
           	<th data-field="state" data-checkbox="true"></th>
           	<%-- <th data-field="id"
            	 data-align="center"
                 data-formatter="actionFormatter"
                 data-events="actionEvents"><fmt:message key="table.operation"/></th> --%>
           <%-- <th data-field="content" data-formatter="nameFormatter"><fmt:message key="organizationWorkflowType.name"/></th> --%>
            <th data-field="workflowType" data-formatter="nameFormatter"><fmt:message key="organizationWorkflowType.workflowType"/></th>
	        <th data-field="organizationCode"><fmt:message key="organizationWorkflowType.organization"/></th>
	        <th data-field="description"><fmt:message key="organizationWorkflowType.desc"/></th>        
	        <th data-field="createdBy"><fmt:message key="organizationWorkflowType.createdBy"/></th>
	        <th data-field="dateAdded" data-formatter="dateTimeFormatter"><fmt:message key="organizationWorkflowType.dateAdded"/></th>
        </tr>
        </thead>
    </table>
</div>
