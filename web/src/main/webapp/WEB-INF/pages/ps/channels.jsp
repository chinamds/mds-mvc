<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="channelList.title"/></title>
    <meta name="menu" content="ChannelMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="channelList.heading"/></h2>

<form method="get" action="${ctx}/sch/channels" id="searchForm" class="form-inline">
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

<p><fmt:message key="channelList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/sch/channelform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="channelList" class="table table-condensed table-striped table-hover" requestURI="" id="channelList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="${ctx}/sch/channelform" media="html"
        paramId="id" paramProperty="id" titleKey="channel.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="channel.id"/>
    <display:column property="BAllContent" sortable="true" titleKey="channel.BAllContent"/>
    <display:column property="BImm" sortable="true" titleKey="channel.BImm"/>
    <display:column property="BIncludeToday" sortable="true" titleKey="channel.BIncludeToday"/>
    <display:column property="channelDesc" sortable="true" titleKey="channel.channelDesc"/>
    <display:column property="channelName" sortable="true" titleKey="channel.channelName"/>
    <display:column property="defPlaylist" sortable="true" titleKey="channel.defPlaylist"/>
    <display:column property="ftpTime" sortable="true" titleKey="channel.ftpTime"/>
    <display:column property="period" sortable="true" titleKey="channel.period"/>
    <display:column property="timeOuts" sortable="true" titleKey="channel.timeOuts"/>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="channelList.channel"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="channelList.channels"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="channelList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="channelList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="channelList.title"/>.pdf</display:setProperty>
</display:table>
