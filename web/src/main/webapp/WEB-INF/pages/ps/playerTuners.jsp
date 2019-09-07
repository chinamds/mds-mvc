<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="playerTunerList.title"/></title>
    <meta name="menu" content="PlayerTunerMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="playerTunerList.heading"/></h2>

<form method="get" action="${ctx}/playerTuners" id="searchForm" class="form-inline">
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

<p><fmt:message key="playerTunerList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/playerTunerform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="playerTunerList" class="table table-condensed table-striped table-hover" requestURI="" id="playerTunerList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="playerTunerform" media="html"
        paramId="id" paramProperty="id" titleKey="playerTuner.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="playerTuner.id"/>
    <display:column property="createdBy" sortable="true" titleKey="playerTuner.createdBy"/>
    <display:column property="dateAdded" sortable="true" titleKey="playerTuner.dateAdded"/>
    <display:column property="dateLastModified" sortable="true" titleKey="playerTuner.dateLastModified"/>
    <display:column property="lastModifiedBy" sortable="true" titleKey="playerTuner.lastModifiedBy"/>
    <display:column property="channelName" sortable="true" titleKey="playerTuner.channelName"/>
    <display:column property="endTime" sortable="true" titleKey="playerTuner.endTime"/>
    <display:column property="output" sortable="true" titleKey="playerTuner.output"/>
    <display:column property="startTime" sortable="true" titleKey="playerTuner.startTime"/>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="playerTunerList.playerTuner"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="playerTunerList.playerTuners"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="playerTunerList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="playerTunerList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="playerTunerList.title"/>.pdf</display:setProperty>
</display:table>
