<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="notificationList.title"/></title>
    <meta name="menu" content="NotificationMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="notificationList.heading"/></h2>

<form method="get" action="${ctx}/notifications" id="searchForm" class="form-inline">
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

<p><fmt:message key="notificationList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/notificationform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="notificationList" class="table table-condensed table-striped table-hover" requestURI="" id="notificationList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="notificationform" media="html"
        paramId="id" paramProperty="id" titleKey="notification.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="notification.id"/>
    <display:column property="content" sortable="true" titleKey="notification.content"/>
    <display:column property="date" sortable="true" titleKey="notification.date"/>
    <display:column property="read" sortable="true" titleKey="notification.read"/>
    <display:column property="source" sortable="true" titleKey="notification.source"/>
    <display:column property="title" sortable="true" titleKey="notification.title"/>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="notificationList.notification"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="notificationList.notifications"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="notificationList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="notificationList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="notificationList.title"/>.pdf</display:setProperty>
</display:table>
