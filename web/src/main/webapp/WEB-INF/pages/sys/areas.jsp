<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="areaList.title"/></title>
    <meta name="menu" content="AreaMenu"/>
</head>

<%-- <c:set var="group" value="grp_treetable" scope="request" /> --%>
<c:set var="group" value="grp_boostrap_table_treegrid" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/sys/area.js"%>
</c:set>

<%-- <ul class="nav nav-tabs">
	<li class="active"><a href="${ctx}/sys/areas/">Area List</a></li>
	<secure:hasPermission name="sys:area:edit"><li><a href="${ctx}/sys/areaform">Area�</a></li></secure:hasPermission>
</ul> --%>

<c:set var="delObject" scope="request"><fmt:message key="areaList.area"/></c:set>
<script type="text/javascript">var msgDelConfirm =
   "<fmt:message key="delete.confirm"><fmt:param value="${delObject}"/></fmt:message>";
</script>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<div class="col">
<h2><fmt:message key="areaList.heading"/></h2>

<div id="searchBox" class="hidden d-none">
	<form method="get" action="${ctx}/sys/areas" id="searchForm" class="form-inline">
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

<p><fmt:message key="areaList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/sys/areaform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <button class="btn btn-danger" id="delete" name="delete">
        <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
    <button id="btnImport" class="btn btn-success" type="button" title="<fmt:message key="button.import"/>">
        <i class="fa fa-file-import"></i> <span class="hidden-xs"><fmt:message key="button.import"/></span></button>
    <button id="btnExport" class="btn btn-info" type="button" title="<fmt:message key="button.export"/>">
        <i class="fa fa-file-export"></i><span class="hidden-xs"><fmt:message key="button.export"/></span></button>
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
	        <div class="form-group">
            	<div class="col-sm-9 col-sm-offset-3">
					<a href="${ctx}/sys/areas/import/template"><fmt:message key="button.downloadtemplate"/></a>
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
            <th data-field="code" data-formatter="codeFormatter"><fmt:message key="area.code"/></th>
            <th data-field="name"><fmt:message key="area.name"/></th>
	        <%-- <th data-field="type"><fmt:message key="area.type"/></th> --%>
	        <%-- <th data-field="permission"><fmt:message key="organization.permission"/></th> --%>
        </tr>
        </thead>
    </table>
</div>
</div>
