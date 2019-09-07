<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="catalogueList.title"/></title>
    <meta name="menu" content="CatalogueMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="catalogueList.heading"/></h2>

<form method="get" action="${ctx}/pl/catalogues" id="searchForm" class="form-inline">
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

<p><fmt:message key="catalogueList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/pl/catalogueform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="catalogueList" class="table table-condensed table-striped table-hover" requestURI="" id="catalogueList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="${ctx}/pl/catalogueform" media="html"
        paramId="id" paramProperty="id" titleKey="catalogue.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="catalogue.id"/>
    <display:column property="BGColor" sortable="true" titleKey="catalogue.BGColor"/>
    <display:column sortProperty="BGMusic" sortable="true" titleKey="catalogue.BGMusic">
        <input type="checkbox" disabled="disabled" <c:if test="${catalogueList.BGMusic}">checked="checked"</c:if>/>
    </display:column>
    <display:column property="bgType" sortable="true" titleKey="catalogue.bgType"/>
    <display:column property="btnAlign" sortable="true" titleKey="catalogue.btnAlign"/>
    <display:column property="btnLng" sortable="true" titleKey="catalogue.btnLng"/>
    <display:column property="btnStyle" sortable="true" titleKey="catalogue.btnStyle"/>
    <display:column property="catalogueDesc" sortable="true" titleKey="catalogue.catalogueDesc"/>
    <display:column property="catalogueName" sortable="true" titleKey="catalogue.catalogueName"/>
    <display:column sortProperty="fontBold" sortable="true" titleKey="catalogue.fontBold">
        <input type="checkbox" disabled="disabled" <c:if test="${catalogueList.fontBold}">checked="checked"</c:if>/>
    </display:column>
    <display:column property="fontColor" sortable="true" titleKey="catalogue.fontColor"/>
    <display:column sortProperty="fontItalic" sortable="true" titleKey="catalogue.fontItalic">
        <input type="checkbox" disabled="disabled" <c:if test="${catalogueList.fontItalic}">checked="checked"</c:if>/>
    </display:column>
    <display:column property="fontName" sortable="true" titleKey="catalogue.fontName"/>
    <display:column property="fontSize" sortable="true" titleKey="catalogue.fontSize"/>
    <display:column sortProperty="fontUnderline" sortable="true" titleKey="catalogue.fontUnderline">
        <input type="checkbox" disabled="disabled" <c:if test="${catalogueList.fontUnderline}">checked="checked"</c:if>/>
    </display:column>
    <display:column property="imageFile" sortable="true" titleKey="catalogue.imageFile"/>
    <display:column sortProperty="interactive" sortable="true" titleKey="catalogue.interactive">
        <input type="checkbox" disabled="disabled" <c:if test="${catalogueList.interactive}">checked="checked"</c:if>/>
    </display:column>
    <display:column property="layoutName" sortable="true" titleKey="catalogue.layoutName"/>
    <display:column property="musicFile" sortable="true" titleKey="catalogue.musicFile"/>
    <display:column property="quantity" sortable="true" titleKey="catalogue.quantity"/>
    <display:column property="screenType" sortable="true" titleKey="catalogue.screenType"/>
    <display:column property="skin" sortable="true" titleKey="catalogue.skin"/>
    <display:column property="skinCode" sortable="true" titleKey="catalogue.skinCode"/>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="catalogueList.catalogue"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="catalogueList.catalogues"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="catalogueList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="catalogueList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="catalogueList.title"/>.pdf</display:setProperty>
</display:table>
