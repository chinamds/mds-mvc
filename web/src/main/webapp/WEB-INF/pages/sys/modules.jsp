<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="moduleList.title"/></title>
    <meta name="menu" content="ModuleMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<div class="col">
<h2><fmt:message key="moduleList.heading"/></h2>

<form method="get" action="${ctx}/modules" id="searchForm" class="form-inline">
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

<p><fmt:message key="moduleList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/moduleform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <button id="btnImport" class="btn btn-primary" type="button">
        <i class="fa fa-file-import"></i> <fmt:message key="button.import"/></button>
    <button id="btnExport" class="btn btn-primary" type="button">
        <i class="fa fa-file-export"></i> <fmt:message key="button.export"/></button>
    <a href='<c:url value="/home"/>' class="btn btn-default visible-xs-block visible-sm-block d-md-none d-xs-block d-sm-block"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="moduleList" class="table table-condensed table-striped table-hover" requestURI="" id="moduleList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="moduleform" media="html"
        paramId="id" paramProperty="id" titleKey="module.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="module.id"/>
    <display:column property="NDescLangId" sortable="true" titleKey="module.NDescLangId"/>
    <display:column property="NTitleLangId" sortable="true" titleKey="module.NTitleLangId"/>
    <display:column property="moduleFlag" sortable="true" titleKey="module.moduleFlag"/>
    <display:column property="moduleGroup" sortable="true" titleKey="module.moduleGroup"/>
    <display:column property="moduleIcon" sortable="true" titleKey="module.moduleIcon"/>
    <display:column property="moduleName" sortable="true" titleKey="module.moduleName"/>
    <display:column property="moduleType" sortable="true" titleKey="module.moduleType"/>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="moduleList.module"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="moduleList.modules"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="moduleList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="moduleList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="moduleList.title"/>.pdf</display:setProperty>
</display:table>
</div>