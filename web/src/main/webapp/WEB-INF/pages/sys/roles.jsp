<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.mds.aiotplayer.sys.model.RoleType" %>

<head>
    <title><fmt:message key="roleList.title"/></title>
    <meta name="menu" content="RoleMenu"/>
</head>
<c:set var="group" value="grp_boostrap_table_treegrid" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/sys/role.js"%>
</c:set>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<div class="col">
<h2><fmt:message key="roleList.heading"/></h2>

<%-- <form method="get" action="${ctx}/sys/roles" id="searchForm" class="form-inline"> --%>
<form id="searchForm" class="form-inline float-right">
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

<p><fmt:message key="roleList.message"/></p>

<div id="actions" class="btn-group">
	<secure:hasPermission name="sys:roles:add">
    <a href='<c:url value="/sys/roleform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    </secure:hasPermission>
    <secure:hasPermission name="sys:roles:delete">
    <button class="btn btn-danger" id="delete" name="delete">
        <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
    </secure:hasPermission>
    <secure:hasPermission name="sys:roles:data_import">
    <button id="btnImport" class="btn btn-success" type="button" title="<fmt:message key="button.import"/>">
        <i class="fa fa-file-import"></i> <span class="hidden-xs"><fmt:message key="button.import"/></span></button>
    </secure:hasPermission>
    <secure:hasPermission name="sys:roles:data_export">
    <button id="btnExport" class="btn btn-info" type="button" title="<fmt:message key="button.export"/>">
        <i class="fa fa-file-export"></i><span class="hidden-xs"><fmt:message key="button.export"/></span></button>
    </secure:hasPermission>
    <a href='<c:url value="/home"/>' class="btn btn-default visible-xs-block visible-sm-block d-md-none d-xs-block d-sm-block"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<div id="importBox" class="hidden d-none">
	<form id="importForm" action="${ctx}/sys/roles/import" method="post" enctype="multipart/form-data"
		style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('<fmt:message key="importform.importing"/>');"><br/>
		<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>
		<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="<fmt:message key="button.import"/>"/>
		<a href="${ctx}/sys/roles/import/template"><fmt:message key="button.downloadtemplate"/></a>
	</form>
</div>

<div class="table-responsive">
	<table id="table">
        <thead>
        <tr>
            <th data-field="state" data-checkbox="true"></th>
            <th data-field="id"
                data-align="center"
                data-formatter="actionFormatter"
                data-events="actionEvents"><fmt:message key="table.operation"/></th>
            <th data-field="name" data-formatter="nameFormatter"><fmt:message key="role.name"/></th>
	        <th data-field="roleType"><fmt:message key="role.type"/></th>       
	        <th data-field="organizationCode"><fmt:message key="role.organization"/></th>        
	        <th data-field="description"><fmt:message key="role.description"/></th>
        </tr>
        </thead>
    </table>
</div>
</div>

