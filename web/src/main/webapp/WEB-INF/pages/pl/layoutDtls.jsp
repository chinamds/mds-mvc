<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="layoutDtlList.title"/></title>
    <meta name="menu" content="LayoutDtlMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="layoutDtlList.heading"/></h2>

<form method="get" action="${ctx}/layoutDtls" id="searchForm" class="form-inline">
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

<p><fmt:message key="layoutDtlList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/layoutDtlform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="layoutDtlList" class="table table-condensed table-striped table-hover" requestURI="" id="layoutDtlList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="layoutDtlform" media="html"
        paramId="id" paramProperty="id" titleKey="layoutDtl.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="layoutDtl.id"/>
    <display:column property="createdBy" sortable="true" titleKey="layoutDtl.createdBy"/>
    <display:column property="dateAdded" sortable="true" titleKey="layoutDtl.dateAdded"/>
    <display:column property="dateLastModified" sortable="true" titleKey="layoutDtl.dateLastModified"/>
    <display:column property="lastModifiedBy" sortable="true" titleKey="layoutDtl.lastModifiedBy"/>
    <display:column property="alpha" sortable="true" titleKey="layoutDtl.alpha"/>
    <display:column property="bottom" sortable="true" titleKey="layoutDtl.bottom"/>
    <display:column property="left" sortable="true" titleKey="layoutDtl.left"/>
    <display:column property="level" sortable="true" titleKey="layoutDtl.level"/>
    <display:column property="right" sortable="true" titleKey="layoutDtl.right"/>
    <display:column property="top" sortable="true" titleKey="layoutDtl.top"/>
    <display:column property="zoneId" sortable="true" titleKey="layoutDtl.zoneId"/>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="layoutDtlList.layoutDtl"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="layoutDtlList.layoutDtls"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="layoutDtlList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="layoutDtlList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="layoutDtlList.title"/>.pdf</display:setProperty>
</display:table>
