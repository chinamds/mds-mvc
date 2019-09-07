<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="layoutMstList.title"/></title>
    <meta name="menu" content="LayoutMstMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="layoutMstList.heading"/></h2>

<form method="get" action="${ctx}/layoutMsts" id="searchForm" class="form-inline">
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

<p><fmt:message key="layoutMstList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/layoutMstform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="layoutMstList" class="table table-condensed table-striped table-hover" requestURI="" id="layoutMstList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="layoutMstform" media="html"
        paramId="id" paramProperty="id" titleKey="layoutMst.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="layoutMst.id"/>
    <display:column property="createdBy" sortable="true" titleKey="layoutMst.createdBy"/>
    <display:column property="dateAdded" sortable="true" titleKey="layoutMst.dateAdded"/>
    <display:column property="dateLastModified" sortable="true" titleKey="layoutMst.dateLastModified"/>
    <display:column property="lastModifiedBy" sortable="true" titleKey="layoutMst.lastModifiedBy"/>
    <display:column property="IL10Bottom" sortable="true" titleKey="layoutMst.IL10Bottom"/>
    <display:column property="IL10Left" sortable="true" titleKey="layoutMst.IL10Left"/>
    <display:column property="IL10Right" sortable="true" titleKey="layoutMst.IL10Right"/>
    <display:column property="IL10Top" sortable="true" titleKey="layoutMst.IL10Top"/>
    <display:column property="IL11Bottom" sortable="true" titleKey="layoutMst.IL11Bottom"/>
    <display:column property="IL11Left" sortable="true" titleKey="layoutMst.IL11Left"/>
    <display:column property="IL11Right" sortable="true" titleKey="layoutMst.IL11Right"/>
    <display:column property="IL11Top" sortable="true" titleKey="layoutMst.IL11Top"/>
    <display:column property="IL12Bottom" sortable="true" titleKey="layoutMst.IL12Bottom"/>
    <display:column property="IL12Left" sortable="true" titleKey="layoutMst.IL12Left"/>
    <display:column property="IL12Right" sortable="true" titleKey="layoutMst.IL12Right"/>
    <display:column property="IL12Top" sortable="true" titleKey="layoutMst.IL12Top"/>
    <display:column property="IL1Bottom" sortable="true" titleKey="layoutMst.IL1Bottom"/>
    <display:column property="IL1Left" sortable="true" titleKey="layoutMst.IL1Left"/>
    <display:column property="IL1Right" sortable="true" titleKey="layoutMst.IL1Right"/>
    <display:column property="IL1Top" sortable="true" titleKey="layoutMst.IL1Top"/>
    <display:column property="IL2Bottom" sortable="true" titleKey="layoutMst.IL2Bottom"/>
    <display:column property="IL2Left" sortable="true" titleKey="layoutMst.IL2Left"/>
    <display:column property="IL2Right" sortable="true" titleKey="layoutMst.IL2Right"/>
    <display:column property="IL2Top" sortable="true" titleKey="layoutMst.IL2Top"/>
    <display:column property="IL3Bottom" sortable="true" titleKey="layoutMst.IL3Bottom"/>
    <display:column property="IL3Left" sortable="true" titleKey="layoutMst.IL3Left"/>
    <display:column property="IL3Right" sortable="true" titleKey="layoutMst.IL3Right"/>
    <display:column property="IL3Top" sortable="true" titleKey="layoutMst.IL3Top"/>
    <display:column property="IL4Bottom" sortable="true" titleKey="layoutMst.IL4Bottom"/>
    <display:column property="IL4Left" sortable="true" titleKey="layoutMst.IL4Left"/>
    <display:column property="IL4Right" sortable="true" titleKey="layoutMst.IL4Right"/>
    <display:column property="IL4Top" sortable="true" titleKey="layoutMst.IL4Top"/>
    <display:column property="IL5Bottom" sortable="true" titleKey="layoutMst.IL5Bottom"/>
    <display:column property="IL5Left" sortable="true" titleKey="layoutMst.IL5Left"/>
    <display:column property="IL5Right" sortable="true" titleKey="layoutMst.IL5Right"/>
    <display:column property="IL5Top" sortable="true" titleKey="layoutMst.IL5Top"/>
    <display:column property="IL6Bottom" sortable="true" titleKey="layoutMst.IL6Bottom"/>
    <display:column property="IL6Left" sortable="true" titleKey="layoutMst.IL6Left"/>
    <display:column property="IL6Right" sortable="true" titleKey="layoutMst.IL6Right"/>
    <display:column property="IL6Top" sortable="true" titleKey="layoutMst.IL6Top"/>
    <display:column property="IL7Bottom" sortable="true" titleKey="layoutMst.IL7Bottom"/>
    <display:column property="IL7Left" sortable="true" titleKey="layoutMst.IL7Left"/>
    <display:column property="IL7Right" sortable="true" titleKey="layoutMst.IL7Right"/>
    <display:column property="IL7Top" sortable="true" titleKey="layoutMst.IL7Top"/>
    <display:column property="IL8Bottom" sortable="true" titleKey="layoutMst.IL8Bottom"/>
    <display:column property="IL8Left" sortable="true" titleKey="layoutMst.IL8Left"/>
    <display:column property="IL8Right" sortable="true" titleKey="layoutMst.IL8Right"/>
    <display:column property="IL8Top" sortable="true" titleKey="layoutMst.IL8Top"/>
    <display:column property="IL9Bottom" sortable="true" titleKey="layoutMst.IL9Bottom"/>
    <display:column property="IL9Left" sortable="true" titleKey="layoutMst.IL9Left"/>
    <display:column property="IL9Right" sortable="true" titleKey="layoutMst.IL9Right"/>
    <display:column property="IL9Top" sortable="true" titleKey="layoutMst.IL9Top"/>
    <display:column property="INoOfParition" sortable="true" titleKey="layoutMst.INoOfParition"/>
    <display:column property="IScreenHeight" sortable="true" titleKey="layoutMst.IScreenHeight"/>
    <display:column property="IScreenWidth" sortable="true" titleKey="layoutMst.IScreenWidth"/>
    <display:column property="ITextZone" sortable="true" titleKey="layoutMst.ITextZone"/>
    <display:column property="groupId" sortable="true" titleKey="layoutMst.groupId"/>
    <display:column property="image" sortable="true" titleKey="layoutMst.image"/>
    <display:column property="imageFile" sortable="true" titleKey="layoutMst.imageFile"/>
    <display:column property="layoutDesc" sortable="true" titleKey="layoutMst.layoutDesc"/>
    <display:column property="layoutName" sortable="true" titleKey="layoutMst.layoutName"/>
    <display:column property="seq" sortable="true" titleKey="layoutMst.seq"/>
    <display:column sortProperty="topMost1" sortable="true" titleKey="layoutMst.topMost1">
        <input type="checkbox" disabled="disabled" <c:if test="${layoutMstList.topMost1}">checked="checked"</c:if>/>
    </display:column>
    <display:column sortProperty="topMost10" sortable="true" titleKey="layoutMst.topMost10">
        <input type="checkbox" disabled="disabled" <c:if test="${layoutMstList.topMost10}">checked="checked"</c:if>/>
    </display:column>
    <display:column sortProperty="topMost11" sortable="true" titleKey="layoutMst.topMost11">
        <input type="checkbox" disabled="disabled" <c:if test="${layoutMstList.topMost11}">checked="checked"</c:if>/>
    </display:column>
    <display:column sortProperty="topMost12" sortable="true" titleKey="layoutMst.topMost12">
        <input type="checkbox" disabled="disabled" <c:if test="${layoutMstList.topMost12}">checked="checked"</c:if>/>
    </display:column>
    <display:column sortProperty="topMost2" sortable="true" titleKey="layoutMst.topMost2">
        <input type="checkbox" disabled="disabled" <c:if test="${layoutMstList.topMost2}">checked="checked"</c:if>/>
    </display:column>
    <display:column sortProperty="topMost3" sortable="true" titleKey="layoutMst.topMost3">
        <input type="checkbox" disabled="disabled" <c:if test="${layoutMstList.topMost3}">checked="checked"</c:if>/>
    </display:column>
    <display:column sortProperty="topMost4" sortable="true" titleKey="layoutMst.topMost4">
        <input type="checkbox" disabled="disabled" <c:if test="${layoutMstList.topMost4}">checked="checked"</c:if>/>
    </display:column>
    <display:column sortProperty="topMost5" sortable="true" titleKey="layoutMst.topMost5">
        <input type="checkbox" disabled="disabled" <c:if test="${layoutMstList.topMost5}">checked="checked"</c:if>/>
    </display:column>
    <display:column sortProperty="topMost6" sortable="true" titleKey="layoutMst.topMost6">
        <input type="checkbox" disabled="disabled" <c:if test="${layoutMstList.topMost6}">checked="checked"</c:if>/>
    </display:column>
    <display:column sortProperty="topMost7" sortable="true" titleKey="layoutMst.topMost7">
        <input type="checkbox" disabled="disabled" <c:if test="${layoutMstList.topMost7}">checked="checked"</c:if>/>
    </display:column>
    <display:column sortProperty="topMost8" sortable="true" titleKey="layoutMst.topMost8">
        <input type="checkbox" disabled="disabled" <c:if test="${layoutMstList.topMost8}">checked="checked"</c:if>/>
    </display:column>
    <display:column sortProperty="topMost9" sortable="true" titleKey="layoutMst.topMost9">
        <input type="checkbox" disabled="disabled" <c:if test="${layoutMstList.topMost9}">checked="checked"</c:if>/>
    </display:column>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="layoutMstList.layoutMst"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="layoutMstList.layoutMsts"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="layoutMstList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="layoutMstList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="layoutMstList.title"/>.pdf</display:setProperty>
</display:table>
