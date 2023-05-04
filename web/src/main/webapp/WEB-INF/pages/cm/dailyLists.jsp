<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="dailyListList.title"/></title>
    <meta name="menu" content="DailyListMenu"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/cm/dailyList.js"%>
</c:set>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<div class="col">
<h2><fmt:message key="dailyListList.heading"/></h2>

<%-- <form method="get" action="${ctx}/dailyLists" id="searchForm" class="form-inline">
<div id="search" class="text-right">
    <span class="col-sm-9">
        <input type="text" size="20" name="q" id="query" value="${param.q}"
               placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
    </span>
    <button id="button.search" class="btn btn-default btn-sm" type="submit">
        <i class="icon-search"></i> <fmt:message key="button.search"/>
    </button>
</div>
</form> --%>
<%@include file="../common/searchbox.jsp"%>

<p><fmt:message key="dailyListList.message"/></p>

<div id="importBox" class="hidden d-none">
	<form id="importForm" action="${ctx}/cm/dailyLists/import" method="post" enctype="multipart/form-data"
		style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('<fmt:message key="importform.importing"/>');"><br/>
		<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>
		<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="<fmt:message key="button.import"/>"/>
		<a href="${ctx}/cm/dailyLists/import/template"><fmt:message key="button.downloadtemplate"/></a>
	</form>
</div>

<div id="actions" class="btn-group">
	<secure:hasPermission name="cm:dailyLists:add">
    <a href='<c:url value="/cm/dailyListform?method=Add"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    </secure:hasPermission>
    <secure:hasPermission name="cm:dailyLists:delete">
    <button class="btn btn-danger" id="delete" name="delete">
        <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
    </secure:hasPermission>
    <secure:hasPermission name="cm:dailyLists:data_import">
    <button id="btnImport" class="btn btn-success hidden-xs" type="button" title="<fmt:message key="button.import"/>">
        <i class="fa fa-file-import"></i> <span class="hidden-xs"><fmt:message key="button.import"/></span></button>
     </secure:hasPermission>
     <secure:hasPermission name="cm:dailyLists:data_export">
    <button id="btnExport" class="btn btn-info hidden-xs" type="button" title="<fmt:message key="button.export"/>">
        <i class="fa fa-file-export"></i><span class="hidden-xs"><fmt:message key="button.export"/></span></button>
    </secure:hasPermission>
    <secure:hasAnyPermissions name="cm:dailyLists:data_import,cm:dailyLists:data_export">
    <div class="btn-group visible-xs-block visible-sm-block">
	    <button type="button" class="btn btn-info dropdown-toggle" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
		    <fmt:message key="button.moreactions"/> <span class="caret"></span></button>
		<ul class="dropdown-menu">
		  <secure:hasPermission name="cm:dailyLists:data_import">	
		  <li><a class="btn-moreactions-import"><i class="fa fa-file-import"></i>&nbsp;<fmt:message key="button.import"/></a></li>
		  </secure:hasPermission>
		  <secure:hasPermission name="cm:dailyLists:data_export">
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
           <%-- <th data-field="content" data-formatter="nameFormatter"><fmt:message key="dailyList.name"/></th> --%>
           <th data-field="thumbnailHtml" data-formatter="thumbnailFormatter"><fmt:message key="dailyList.contentName"/></th>
        <th data-field="date"><fmt:message key="dailyList.date"/></th>       
<c:if test="${independentSpaceForDailyList}">
	        <th data-field="gallery"><fmt:message key="dailyList.gallery"/></th>
</c:if>        
<c:if test="${not independentSpaceForDailyList}">
			<th data-field="organization"><fmt:message key="dailyList.organization"/></th>
</c:if>
        <th data-field="createdBy"><fmt:message key="dailyList.createdBy"/></th>
        <th data-field="dateAdded" data-formatter="dateTimeFormatter"><fmt:message key="dailyList.dateAdded"/></th>
        </tr>
        </thead>
    </table>
</div>
</div>