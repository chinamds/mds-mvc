<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="appSettingList.title"/></title>
    <meta name="menu" content="AppSettingMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="appSettingList.heading"/></h2>

<form method="get" action="${ctx}/appSettings" id="searchForm" class="form-inline">
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

<p><fmt:message key="appSettingList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/appSettingform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="appSettingList" class="table table-condensed table-striped table-hover" requestURI="" id="appSettingList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="appSettingform" media="html"
        paramId="id" paramProperty="id" titleKey="appSetting.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="appSetting.id"/>
    <display:column property="settingName" sortable="true" titleKey="appSetting.settingName"/>
    <display:column property="settingValue" sortable="true" titleKey="appSetting.settingValue"/>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="appSettingList.appSetting"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="appSettingList.appSettings"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="appSettingList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="appSettingList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="appSettingList.title"/>.pdf</display:setProperty>
</display:table>
