<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="permissionList.title"/></title>
    <meta name="menu" content="PermissionMenu"/>
</head>

<c:set var="group" value="grp_boostrap_table_treegrid" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/sys/permission.js"%>
</c:set>

<div class="col">
	<c:if test="{'$'}{not empty searchError}">
	    <div class="alert alert-danger alert-dismissable">
	        <a href="#" data-dismiss="alert" class="close">&times;</a>
	        <c:out value="{'$'}{searchError}"/>
	    </div>
	</c:if>
	
	<h2><fmt:message key="permissionList.heading"/></h2>
	
	<%-- <form method="get" action="${ctx}/sys/permissions" id="searchForm" class="form-inline"> --%>
	<form id="searchForm" class="form-inline float-end">
		<div id="search" class="input-group">
			 <input type="text" size="20" name="q" id="query" value="${param.q}"
		               placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
		    <span class="input-group-append">
				<button id="buttonSearch" class="btn btn-default btn-sm">
			        <i class="fa fa-search"></i> <fmt:message key="button.search"/>
				</button>
			</span>
		</div>
	</form>
	
	<p><fmt:message key="permissionList.message"/></p>
	
	<div id="actions" class="btn-group">
	<secure:hasPermission name="sys:permissions:add">
	    <a href='<c:url value="/sys/permissionform"/>' class="btn btn-primary">
	        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    </secure:hasPermission>
    <secure:hasPermission name="sys:permissions:delete">
	    <button class="btn btn-danger" id="delete" name="delete">
	        <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
    </secure:hasPermission>
    <secure:hasPermission name="sys:permissions:data_import">
	    <button id="btnImport" class="btn btn-success" type="button" title="<fmt:message key="button.import"/>">
	        <i class="fa fa-file-import"></i> <span class="hidden-xs"><fmt:message key="button.import"/></span></button>
    </secure:hasPermission>
    <secure:hasPermission name="sys:permissions:data_export">
	    <button id="btnExport" class="btn btn-info" type="button" title="<fmt:message key="button.export"/>">
	        <i class="fa fa-file-export"></i><span class="hidden-xs"><fmt:message key="button.export"/></span></button>
    </secure:hasPermission>
	    <a href='<c:url value="/home"/>' class="btn btn-default visible-xs-block visible-sm-block d-md-none d-xs-block d-sm-block"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
	</div>
	
	<div id="importBox" class="hidden d-none">
		<form id="importForm" action="${ctx}/sys/permissions/import" method="post" enctype="multipart/form-data"
			style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('<fmt:message key="importform.importing"/>');"><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="<fmt:message key="button.import"/>"/>
			<a href="${ctx}/sys/permissions/import/template"><fmt:message key="button.downloadtemplate"/></a>
		</form>
	</div>
	
	<div class="table-responsive">
		<table id="table">
	        <thead>
	        <tr>
	            <th data-field="state" data-checkbox="true"></th>
	            <th data-field="name" data-formatter="nameFormatter"><fmt:message key="permission.name"/></th>
		        <th data-field="permission"><fmt:message key="permission.permission"/></th>
		        <%-- <th data-field="show"><fmt:message key="permission.show"/></th>    data-filter-control="true" data-filter-show-clear="false" data-search="true" --%>        
		        <th data-field="description"><fmt:message key="permission.description"/></th>
	        </tr>
	        </thead>
	    </table>
	</div>
</div>
