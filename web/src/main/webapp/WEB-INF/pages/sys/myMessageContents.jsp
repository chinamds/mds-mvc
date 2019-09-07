<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="myMessageContentList.title"/></title>
    <meta name="menu" content="MyMessageContentMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="myMessageContentList.heading"/></h2>

<form method="get" action="${ctx}/myMessageContents" id="searchForm" class="form-inline">
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

<p><fmt:message key="myMessageContentList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/myMessageContentform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="myMessageContentList" class="table table-condensed table-striped table-hover" requestURI="" id="myMessageContentList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="myMessageContentform" media="html"
        paramId="id" paramProperty="id" titleKey="myMessageContent.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="myMessageContent.id"/>
    <display:column property="content" sortable="true" titleKey="myMessageContent.content"/>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="myMessageContentList.myMessageContent"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="myMessageContentList.myMessageContents"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="myMessageContentList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="myMessageContentList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="myMessageContentList.title"/>.pdf</display:setProperty>
</display:table>
