<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="organizationList.title"/></title>
    <meta name="menu" content="OrganizationMenu"/>
</head>

<c:set var="group" value="grp_boostrap_table_treegrid" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/sys/organization.js"%>
</c:set>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<div class="col">
<h2><fmt:message key="organizationList.heading"/></h2>

<div id="searchBox" class="hidden d-none">
	<form method="get" action="${ctx}/sys/organizations" id="searchForm" class="form-inline">
	<div id="search" class="text-right">
	    <span class="col-sm-9">
	        <input type="text" size="20" name="q" id="query" value="${param.q}"
	               placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
	    </span>
	    <button id="button.search" class="btn btn-default btn-sm" type="submit">
	        <i class="fa fa-search"></i> <fmt:message key="button.search"/>
	    </button>
	</div>
	</form>
</div>

<p><fmt:message key="organizationList.message"/></p>

<div id="actions" class="btn-group">
	<secure:hasPermission name="sys:organizations:add">
    <a href='<c:url value="/sys/organizationform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    </secure:hasPermission>
    <secure:hasPermission name="sys:organizations:delete">
    <button class="btn btn-danger" id="delete" name="delete">
        <i class="fa fa-trash"></i> <fmt:message key="button.delete"/></button>
    </secure:hasPermission>
    <secure:hasPermission name="sys:organizations:data_import">
    <button id="btnImport" class="btn btn-success" type="button">
        <i class="fa fa-file-import icon-red"></i><span class="hidden-xs"><fmt:message key="button.import"/></span></button>
    </secure:hasPermission>
    <secure:hasPermission name="sys:organizations:data_export">
    <button id="btnExport" class="btn btn-info" type="button">
        <i class="fa fa-file-export"></i><span class="hidden-xs"><fmt:message key="button.export"/></span></button>
    </secure:hasPermission>
    <a href='<c:url value="/home"/>' class="btn btn-default visible-xs-block visible-sm-block d-md-none d-xs-block d-sm-block"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<div id="importBox" class="hidden d-none">
	<form id="importForm" action="${ctx}/sys/organizations/import" method="post" enctype="multipart/form-data"
		style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('<fmt:message key="importform.importing"/>');"><br/>
		<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>
		<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="<fmt:message key="button.import"/>"/>
		<a href="${ctx}/sys/organizations/import/template"><fmt:message key="button.downloadtemplate"/></a>
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
            <th data-field="code" data-formatter="codeFormatter"><fmt:message key="organization.code"/></th>
            <th data-field="name"><fmt:message key="organization.name"/></th>
            <th data-field="area"><fmt:message key="organization.area"/></th>
	        <th data-field="header"><fmt:message key="organization.header"/></th>
	        <th data-field="preferredlanguage"><fmt:message key="organization.preferredlanguage"/></th>
	        <th data-field="available"><fmt:message key="organization.available"/></th>
	        <%-- <th data-field="permission"><fmt:message key="organization.permission"/></th> --%>
        </tr>
        </thead>
    </table>
</div>
</div>