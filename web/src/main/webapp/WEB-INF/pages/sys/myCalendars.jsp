<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="myCalendarList.title"/></title>
    <meta name="menu" content="MyCalendarMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="myCalendarList.heading"/></h2>

<form method="get" action="${ctx}/myCalendars" id="searchForm" class="form-inline">
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

<p><fmt:message key="myCalendarList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/sys/myCalendarform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="myCalendarList" class="table table-condensed table-striped table-hover" requestURI="" id="myCalendarList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="${ctx}/sys/myCalendarform" media="html"
        paramId="id" paramProperty="id" titleKey="myCalendar.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="myCalendar.id"/>
    <display:column property="title" sortable="true" titleKey="myCalendar.title"/>
    <display:column sortProperty="startDate" sortable="true" titleKey="myCalendar.startDate">
         <fmt:formatDate value="${myCalendarList.startDate}" pattern="${datePattern}"/>
    </display:column>
    <display:column property="backgroundColor" sortable="true" titleKey="myCalendar.backgroundColor"/>
    <display:column property="details" sortable="true" titleKey="myCalendar.details"/>
    <display:column property="duration" sortable="true" titleKey="myCalendar.duration"/>
    <display:column property="endTime" sortable="true" titleKey="myCalendar.endTime"/>
    <display:column property="startTime" sortable="true" titleKey="myCalendar.startTime"/>
    <display:column property="textColor" sortable="true" titleKey="myCalendar.textColor"/>   
    <display:column property="createdBy" sortable="true" titleKey="myCalendar.createdBy"/>
    <display:column property="dateAdded" sortable="true" titleKey="myCalendar.dateAdded"/>
    <display:column property="dateLastModified" sortable="true" titleKey="myCalendar.dateLastModified"/>
    <display:column property="lastModifiedBy" sortable="true" titleKey="myCalendar.lastModifiedBy"/>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="myCalendarList.myCalendar"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="myCalendarList.myCalendars"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="myCalendarList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="myCalendarList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="myCalendarList.title"/>.pdf</display:setProperty>
</display:table>
