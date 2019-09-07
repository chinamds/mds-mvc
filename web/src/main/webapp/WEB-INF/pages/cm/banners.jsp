<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="bannerList.title"/></title>
    <meta name="menu" content="BannerMenu"/>
</head>

<c:set var="group" value="grp_dialog" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/cm/banner.js"%>
</c:set>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="bannerList.heading"/></h2>

<form method="get" action="${ctx}/cm/banners" id="searchForm" class="form-inline">
<div id="search" class="input-group text-right">
    <%-- <span class="col-sm-9">
        <input type="text" size="20" name="q" id="query" value="${param.q}"
               placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
    </span> --%>
    <input type="text" size="20" name="q" id="query" value="${param.q}"
               placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
    <span class="input-group-btn">
	    <button id="button.search" class="btn btn-default btn-sm" type="submit">
	        <i class="fa fa-search"></i> <fmt:message key="button.search"/>
		</button>
	</span>
</div>
</form>

<p><fmt:message key="bannerList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/cm/bannerform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <button id="btnImport" class="btn btn-default" type="button">
        <i class="fa fa-file-import"></i> <fmt:message key="button.import"/></button>
    <button id="btnExport" class="btn btn-default" type="button">
        <i class="fa fa-file-export"></i> <fmt:message key="button.export"/></button>
    <a href='<c:url value="/home"/>' class="btn btn-default visible-xs-block visible-sm-block d-md-none d-xs-block d-sm-block"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<div id="importBox" class="hidden d-none">
	<form id="importForm" action="${ctx}/cm/banners/import" method="post" enctype="multipart/form-data"
		style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('<fmt:message key="importform.importing"/>');"><br/>
		<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>
		<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="<fmt:message key="button.import"/>"/>
		<a href="${ctx}/cm/banners/import/template"><fmt:message key="button.downloadtemplate"/></a>
	</form>
</div>

<div class="table-responsive">
<table id="table" class="table table-striped table-bordered table-condensed">
	<thead><tr><th class="sort content"><fmt:message key="bannerList.content"/></th><th><fmt:message key="bannerList.desc"/></th><th class="sort dateAdded"><fmt:message key="bannerList.createDate"/></th><th class="sort dateLastModified"><fmt:message key="bannerList.lastModify"/></th><secure:hasPermission name="cm:banner:edit"><th><fmt:message key="table.operation"/></th></secure:hasPermission></tr></thead>
	<tbody>
	<c:forEach items="${page.content}" var="banner">
		<tr>
			<td><a href="${ctx}/cm/bannerform?id=${banner.id}">${banner.content}</a></td>
			<td>${banner.desc}</td>
			<td>${banner.dateAdded}</td>
			<td>${user.dateLastModified}</td>
			<secure:hasPermission name="cm:banner:edit"><td>
   				<a href="${ctx}/cm/bannerform?id=${banner.id}"><fmt:message key="button.edit"/></a>
				<a href="${ctx}/cm/bannerform/delete?id=${banner.id}" onclick="return confirmx('', this.href)"><fmt:message key="button.delete"/></a>
			</td></secure:hasPermission>
		</tr>
	</c:forEach>
	</tbody>
</table>
</div>
<%-- <div class="pagination">${page}</div> --%>
<sys:page page="${page}" />

<%-- <display:table name="bannerList" class="table table-condensed table-striped table-hover" requestURI="" id="bannerList" export="true" pagesize="25">    
    <display:column property="content" sortable="true" href="bannerform" media="html"
        paramId="id" paramProperty="id" titleKey="banner.content"/>
    <display:column property="content" media="csv excel xml pdf" titleKey="banner.content"/>
    <display:column property="desc" sortable="true" titleKey="banner.desc"/>
    <display:column property="userCode" sortable="true" titleKey="banner.userCode"/> 
    <display:column property="groupCode" sortable="true" titleKey="banner.groupCode"/>
    <display:column property="createDate" sortable="true" titleKey="banner.createDate"/>   
    <display:column property="lastModify" sortable="true" titleKey="banner.lastModify"/>   
    
    <display:setProperty name="paging.banner.item_name"><fmt:message key="bannerList.banner"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="bannerList.banners"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="bannerList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="bannerList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="bannerList.title"/>.pdf</display:setProperty>
</display:table> --%>
