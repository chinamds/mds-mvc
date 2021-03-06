<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="playerOutputList.title"/></title>
    <meta name="menu" content="PlayerOutputMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="playerOutputList.heading"/></h2>

<form method="get" action="${ctx}/playerOutputs" id="searchForm" class="form-inline">
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

<p><fmt:message key="playerOutputList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/playerOutputform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="playerOutputList" class="table table-condensed table-striped table-hover" requestURI="" id="playerOutputList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="playerOutputform" media="html"
        paramId="id" paramProperty="id" titleKey="playerOutput.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="playerOutput.id"/>
    <display:column property="createdBy" sortable="true" titleKey="playerOutput.createdBy"/>
    <display:column property="dateAdded" sortable="true" titleKey="playerOutput.dateAdded"/>
    <display:column property="dateLastModified" sortable="true" titleKey="playerOutput.dateLastModified"/>
    <display:column property="lastModifiedBy" sortable="true" titleKey="playerOutput.lastModifiedBy"/>
    <display:column property="location" sortable="true" titleKey="playerOutput.location"/>
    <display:column property="name" sortable="true" titleKey="playerOutput.name"/>
    <display:column property="output" sortable="true" titleKey="playerOutput.output"/>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="playerOutputList.playerOutput"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="playerOutputList.playerOutputs"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="playerOutputList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="playerOutputList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="playerOutputList.title"/>.pdf</display:setProperty>
</display:table>
