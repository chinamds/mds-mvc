<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="playerGroup2PlayerList.title"/></title>
    <meta name="menu" content="PlayerGroup2PlayerMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="playerGroup2PlayerList.heading"/></h2>

<form method="get" action="${ctx}/playerGroup2Players" id="searchForm" class="form-inline">
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

<p><fmt:message key="playerGroup2PlayerList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/playerGroup2Playerform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="playerGroup2PlayerList" class="table table-condensed table-striped table-hover" requestURI="" id="playerGroup2PlayerList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="playerGroup2Playerform" media="html"
        paramId="id" paramProperty="id" titleKey="playerGroup2Player.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="playerGroup2Player.id"/>
    <display:column property="createdBy" sortable="true" titleKey="playerGroup2Player.createdBy"/>
    <display:column property="dateAdded" sortable="true" titleKey="playerGroup2Player.dateAdded"/>
    <display:column property="dateLastModified" sortable="true" titleKey="playerGroup2Player.dateLastModified"/>
    <display:column property="lastModifiedBy" sortable="true" titleKey="playerGroup2Player.lastModifiedBy"/>
    <display:column sortProperty="dateFrom" sortable="true" titleKey="playerGroup2Player.dateFrom">
         <fmt:formatDate value="${playerGroup2PlayerList.dateFrom}" pattern="${datePattern}"/>
    </display:column>
    <display:column sortProperty="dateTo" sortable="true" titleKey="playerGroup2Player.dateTo">
         <fmt:formatDate value="${playerGroup2PlayerList.dateTo}" pattern="${datePattern}"/>
    </display:column>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="playerGroup2PlayerList.playerGroup2Player"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="playerGroup2PlayerList.playerGroup2Players"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="playerGroup2PlayerList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="playerGroup2PlayerList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="playerGroup2PlayerList.title"/>.pdf</display:setProperty>
</display:table>
