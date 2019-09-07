<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="productList.title"/></title>
    <meta name="menu" content="ProductMenu"/>
</head>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<h2><fmt:message key="productList.heading"/></h2>

<form method="get" action="${ctx}/pl/products" id="searchForm" class="form-inline">
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

<p><fmt:message key="productList.message"/></p>

<div id="actions" class="btn-group">
    <a href='<c:url value="/pl/productform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    <a href='<c:url value="/home"/>' class="btn btn-default"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<display:table name="productList" class="table table-condensed table-striped table-hover" requestURI="" id="productList" export="true" pagesize="25">
    <display:column property="id" sortable="true" href="${ctx}/pl/productform" media="html"
        paramId="id" paramProperty="id" titleKey="product.id"/>
    <display:column property="id" media="csv excel xml pdf" titleKey="product.id"/>
    <display:column property="arrProductName" sortable="true" titleKey="product.arrProductName"/>
    <display:column property="btnEvent" sortable="true" titleKey="product.btnEvent"/>
    <display:column property="imageFile" sortable="true" titleKey="product.imageFile"/>
    <display:column property="language" sortable="true" titleKey="product.language"/>
    <display:column property="productDesc" sortable="true" titleKey="product.productDesc"/>
    <display:column property="productIndex" sortable="true" titleKey="product.productIndex"/>
    <display:column property="productName" sortable="true" titleKey="product.productName"/>

    <display:setProperty name="paging.banner.item_name"><fmt:message key="productList.product"/></display:setProperty>
    <display:setProperty name="paging.banner.items_name"><fmt:message key="productList.products"/></display:setProperty>

    <display:setProperty name="export.excel.filename"><fmt:message key="productList.title"/>.xls</display:setProperty>
    <display:setProperty name="export.csv.filename"><fmt:message key="productList.title"/>.csv</display:setProperty>
    <display:setProperty name="export.pdf.filename"><fmt:message key="productList.title"/>.pdf</display:setProperty>
</display:table>
