<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="neutralResourceList.title"/></title>
    <meta name="menu" content="NeutralResourceMenu"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/i18n/neutralresource.js"%>
</c:set>

<div class="col">
<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="neutralResourceList.heading"/></h2>

<form method="get" action="${ctx}/i18n/neutralResources" id="searchForm" class="form-inline float-right">
	<div id="search" class="input-group">
		 <input type="text" size="20" name="q" id="query" value="${param.q}"
	               placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
	    <div class="input-group-append">
		    <button id="button.search" class="btn btn-default btn-sm" type="submit">
		        <i class="fa fa-search"></i> <fmt:message key="button.search"/>
			</button>
		</div>
	</div>
</form>

<p><fmt:message key="neutralResourceList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/i18n/neutralResourceform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <button class="btn btn-danger" id="delete" name="delete">
        <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
    <button id="btnInit" class="btn btn-warning hidden-xs" type="button">
        <i class="fa fa-exchange-alt"></i> <fmt:message key="button.initialize"/></button>
    <button id="btnImport" class="btn btn-success hidden-xs" type="button" title="<fmt:message key="button.import"/>">
        <i class="fa fa-file-import"></i><fmt:message key="button.import"/></button>
    <button id="btnExport" class="btn btn-info hidden-xs" type="button" title="<fmt:message key="button.export"/>">
        <i class="fa fa-file-export"></i><fmt:message key="button.export"/></button>
    <div class="btn-group visible-xs-block visible-sm-block d-block d-sm-block d-md-none">
	    <button type="button" class="btn btn-info dropdown-toggle" id="dropdownMenuMore" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
		    <fmt:message key="button.moreactions"/> <span class="caret"></span></button>
		<div class="dropdown-menu" aria-labelledby="dropdownMenuMore">
		  <a class="dropdown-item btn-moreactions-import"><i class="fa fa-file-import"></i>&nbsp;<fmt:message key="button.import"/></a>
		  <a class="dropdown-item btn-moreactions-export"><i class="fa fa-file-export"></i>&nbsp;<fmt:message key="button.export"/></a>
		  <div class="dropdown-divider"></div>
		  <a class="dropdown-item btn-moreactions-init"><i class="fa fa-exchange-alt"></i>&nbsp;<fmt:message key="button.initialize"/></a>
		</div>
	</div>
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
					<a href="${ctx}/i18n/neutralResources/import/template"><fmt:message key="button.downloadtemplate"/></a>
				</div>
			</div>
		</div>
	</form>
</div>

<div class="table-responsive">
	<table id="table" data-filter-control="true" data-filter-show-clear="true" data-filter="true">
        <thead>
        <tr>
            <th data-field="state" data-checkbox="true"></th>
            <th data-field="resourceKey" data-formatter="nameFormatter"><fmt:message key="neutralResource.resourceKey"/></th>
	        <th data-field="resourceClass"><fmt:message key="neutralResource.resourceClass"/></th>       
	        <th data-field="value"><fmt:message key="neutralResource.value"/></th>
        </tr>
        </thead>
    </table>
</div>
</div>