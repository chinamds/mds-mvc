<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="localizedResourceList.title"/></title>
    <meta name="menu" content="LocalizedResourceMenu"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/i18n/localizedresource.js"%>
</c:set>
<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<div class="col">
<h2><fmt:message key="localizedResourceList.heading"/></h2>

<%-- <form method="get" action="${ctx}/i18n/localizedResources" id="searchForm" class="form-inline float-right">
<div id="search" class="input-group">
    <input type="text" size="20" name="q" id="query" value="${param.q}"
               placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
    <div class="input-group-append">
	    <button id="button.search" class="btn btn-default btn-sm" type="submit">
	        <i class="fa fa-search"></i> <fmt:message key="button.search"/>
		</button>
	</div>
</div>
</form> --%>

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

<p><fmt:message key="localizedResourceList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/i18n/localizedResourceform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <button class="btn btn-danger" id="delete" name="delete">
        <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
    <button id="btnInit" class="btn btn-warning hidden-xs" type="button">
        <i class="fa fa-exchange-alt"></i> <fmt:message key="button.initialize"/></button>
    <button id="btnImport" class="btn btn-success hidden-xs" type="button" title="<fmt:message key="button.import"/>">
        <i class="fa fa-file-import"></i> <span class="hidden-xs"><fmt:message key="button.import"/></span></button>
    <button id="btnExport" class="btn btn-info hidden-xs" type="button" title="<fmt:message key="button.export"/>">
        <i class="fa fa-file-export"></i><span class="hidden-xs"><fmt:message key="button.export"/></span></button>
    <div class="btn-group visible-xs-block visible-sm-block">
	    <button type="button" class="btn btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
		    <fmt:message key="button.moreactions"/> <span class="caret"></span></button>
		<ul class="dropdown-menu">
		  <li><a class="btn-moreactions-import"><i class="fa fa-file-import"></i>&nbsp;<fmt:message key="button.import"/></a></li>
		  <li><a class="btn-moreactions-export"><i class="fa fa-file-export"></i>&nbsp;<fmt:message key="button.export"/></a></li>
		  <li role="separator" class="divider"></li>
		  <li><a class="btn-moreactions-init"><i class="fa fa-exchange-alt"></i>&nbsp;<fmt:message key="button.initialize"/></a></li>
		</ul>
	</div>
    <a href='<c:url value="/home"/>' class="btn btn-default visible-xs-block visible-sm-block d-md-none d-xs-block d-sm-block"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<div id="importBox" class="hidden d-none">
	<form id="importForm" action="${ctx}/i18n/localizedResources/import" method="post" enctype="multipart/form-data"
		style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('<fmt:message key="importform.importing"/>');"><br/>
		<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>
		<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="<fmt:message key="button.import"/>"/>
		<a href="${ctx}/i18n/localizedResources/import/template"><fmt:message key="button.downloadtemplate"/></a>
	</form>
</div>

<div class="table-responsive">
	<table id="table" data-filter-control="true" data-filter-show-clear="true" data-filter="true">
        <thead>
        <tr>
            <th data-field="state" data-checkbox="true"></th>
            <th data-field="resourceKey" data-formatter="nameFormatter" data-filter-control="input"><fmt:message key="neutralResource.resourceKey"/></th>
	        <th data-field="cultureCode" data-filter-control="select"><fmt:message key="culture.cultureCode"/></th>
	        <%-- <th data-field="show"><fmt:message key="localizedResource.show"/></th>   --%>        
	        <th data-field="value" data-filter-control="input"><fmt:message key="localizedResource.value"/></th>
        </tr>
        </thead>
    </table>
</div>
</div>