<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="menuFunctionPermissionList.title"/></title>
    <meta name="menu" content="MenuFunctionPermissionMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="menuFunctionPermissionList.heading"/></h2>

<form method="get" action="${ctx}/menuFunctionPermissions" id="searchForm" class="form-inline">
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

<p><fmt:message key="menuFunctionPermissionList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/menuFunctionPermissionform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="menuFunctionPermissionList" class="table table-condensed table-striped table-hover" requestURI="" id="menuFunctionPermissionList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="menuFunctionPermissionform" media="html"
        paramId="id" paramProperty="id" titleKey="menuFunctionPermission.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="menuFunctionPermission.id"/>
    <display:column property="createdBy" sortable="true" titleKey="menuFunctionPermission.createdBy"/>
    <display:column property="dateAdded" sortable="true" titleKey="menuFunctionPermission.dateAdded"/>
    <display:column property="dateLastModified" sortable="true" titleKey="menuFunctionPermission.dateLastModified"/>
    <display:column property="lastModifiedBy" sortable="true" titleKey="menuFunctionPermission.lastModifiedBy"/>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="menuFunctionPermissionList.menuFunctionPermission"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="menuFunctionPermissionList.menuFunctionPermissions"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="menuFunctionPermissionList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="menuFunctionPermissionList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="menuFunctionPermissionList.title"/>.pdf</display:setProperty>
</display:table>
