<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="taskDefinitionList.title"/></title>
    <meta name="menu" content="TaskDefinitionMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<div class="col">
<h2><fmt:message key="taskDefinitionList.heading"/></h2>

<form method="get" action="${ctx}/taskDefinitions" id="searchForm" class="form-inline">
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

<p><fmt:message key="taskDefinitionList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/taskDefinitionform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="taskDefinitionList" class="table table-condensed table-striped table-hover" requestURI="" id="taskDefinitionList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="taskDefinitionform" media="html"
        paramId="id" paramProperty="id" titleKey="taskDefinition.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="taskDefinition.id"/>
    <display:column property="createdBy" sortable="true" titleKey="taskDefinition.createdBy"/>
    <display:column property="dateAdded" sortable="true" titleKey="taskDefinition.dateAdded"/>
    <display:column property="dateLastModified" sortable="true" titleKey="taskDefinition.dateLastModified"/>
    <display:column property="lastModifiedBy" sortable="true" titleKey="taskDefinition.lastModifiedBy"/>
    <display:column property="beanClass" sortable="true" titleKey="taskDefinition.beanClass"/>
    <display:column property="beanName" sortable="true" titleKey="taskDefinition.beanName"/>
    <display:column property="cron" sortable="true" titleKey="taskDefinition.cron"/>
    <display:column property="description" sortable="true" titleKey="taskDefinition.description"/>
    <display:column property="methodName" sortable="true" titleKey="taskDefinition.methodName"/>
    <display:column property="name" sortable="true" titleKey="taskDefinition.name"/>
    <display:column property="start" sortable="true" titleKey="taskDefinition.start"/>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="taskDefinitionList.taskDefinition"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="taskDefinitionList.taskDefinitions"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="taskDefinitionList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="taskDefinitionList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="taskDefinitionList.title"/>.pdf</display:setProperty>
</display:table>
</div>