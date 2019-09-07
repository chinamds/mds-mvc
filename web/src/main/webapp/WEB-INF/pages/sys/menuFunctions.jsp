<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="menuFunctionList.title"/></title>
    <meta name="menu" content="MenuFunctionMenu"/>
</head>

<c:set var="group" value="grp_boostrap_table_treegrid" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/sys/menuFunction.js"%>
</c:set>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<div class="col">
<h2><fmt:message key="menuFunctionList.heading"/>${suffixWro}</h2>

<div id="searchBox" class="hidden d-none">
	<form method="get" action="${ctx}/sys/menuFunctions" id="searchForm" class="form-inline">
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

<p><fmt:message key="menuFunctionList.message"/></p>

<div id="actions" class="btn-group">
	<secure:hasPermission name="sys:menuFunctions:add">	
    <a href='<c:url value="/sys/menuFunctionform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    </secure:hasPermission>
    <secure:hasPermission name="sys:menuFunctions:delete">
    <button class="btn btn-danger" id="delete" name="delete">
        <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
    </secure:hasPermission>
    <secure:hasPermission name="sys:menuFunctions:data_import">
    <button id="btnImport" class="btn btn-success" type="button" title="<fmt:message key="button.import"/>">
        <i class="fa fa-file-import"></i> <span class="hidden-xs"> <fmt:message key="button.import"/></span></button>
    </secure:hasPermission>
    <secure:hasPermission name="sys:menuFunctions:data_export">
    <button id="btnExport" class="btn btn-info" type="button" title="<fmt:message key="button.export"/>">
        <i class="fa fa-file-export"></i><span class="hidden-xs"> <fmt:message key="button.export"/></span></button>
    </secure:hasPermission>
    <a href='<c:url value="/home"/>' class="btn btn-default visible-xs-block visible-sm-block d-md-none d-xs-block d-sm-block"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<div id="importBox" class="hidden d-none">
	<form id="importForm" method="post" enctype="multipart/form-data"
		class="form-horizontal"><br/>
		<div class="form-group">
			<label class="control-label col-sm-3"><fmt:message key="import.file"/></label>
			<div class="col-sm-9">
				<input name="importFile" id="uploadFile" type="file" class="file" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel">
				<p class="help-block"><fmt:message key="import.information" /></p><br/><br/>
			</div>
			<%-- <button type="submit" class="btn btn-primary" id="btnImportSubmit" name="btnImportSubmit" >
			onsubmit="loading('<fmt:message key="importform.importing"/>');"
			action="${ctx}/sys/menuFunctions/import" col-sm-4
	            <i class="fa fa-check icon-white"></i> <fmt:message key="button.import"/>
	        </button> --%>
	        <div class="form-group">
            	<div class="col-sm-9 col-sm-offset-3">
					<a href="${ctx}/sys/menuFunctions/import/template"><fmt:message key="button.downloadtemplate"/></a>
				</div>
			</div>
		</div>
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
            <th data-field="code"><fmt:message key="menuFunction.code"/></th>
            <th data-field="title" data-formatter="codeFormatter"><fmt:message key="menuFunction.title"/></th>
	        <th data-field="href"><fmt:message key="menuFunction.href"/></th>
	        <th data-field="sort"><fmt:message key="menuFunction.sort"/></th>
	        <th data-field="isShow"><fmt:message key="menuFunction.isShow"/></th>
	        <%-- <th data-field="permission"><fmt:message key="menuFunction.permission"/></th> --%>
        </tr>
        </thead>
    </table>
</div>
</div>