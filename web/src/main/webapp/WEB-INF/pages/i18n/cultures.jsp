<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="cultureList.title"/></title>
    <meta name="menu" content="CultureMenu"/>
</head>

<div class="col">
	<c:if test="{'$'}{not empty searchError}">
	    <div class="alert alert-danger alert-dismissable">
	        <a href="#" data-dismiss="alert" class="close">&times;</a>
	        <c:out value="{'$'}{searchError}"/>
	    </div>
	</c:if>
	
	<h2><fmt:message key="cultureList.heading"/></h2>
	
	<form method="get" action="${ctx}/i18n/cultures" id="searchForm" class="form-inline float-right">
	<div id="search" class="input-group">
		<input type="text" size="20" name="q" id="query" value="${param.q}"
	               placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
	    <span class="input-group-append">
		    <button id="button.search" class="btn btn-default btn-sm" type="submit">
		        <i class="fa fa-search"></i> <fmt:message key="button.search"/>
			</button>
		</span>
	</div>
	</form>
	
	<p><fmt:message key="cultureList.message"/></p>
	
	<div id="actions" class="btn-group">
	    <a href='<c:url value="/i18n/cultureform"/>' class="btn btn-primary">
	        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
	    <a href='<c:url value="/home"/>' class="btn btn-default visible-xs-block visible-sm-block d-md-none d-xs-block d-sm-block"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
	</div>
	
	<div id="importBox" class="hidden d-none">
		<form id="importForm" method="post" enctype="multipart/form-data"
			class="form-horizontal"><br/>
			<div class="form-group">
				<label class="control-label col-sm-3"><fmt:message key="import.file"/></label>
				<div class="col-sm-9">
					<input name="importFile" id="uploadFile" type="file" class="file" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel">
					<p class="help-block"><fmt:message key="import.information" /></p><br/><br/>
				</div>
		        <div class="form-group">
	            	<div class="col-sm-9 col-sm-offset-3">
						<a href="${ctx}/i18n/cultures/import/template"><fmt:message key="button.downloadtemplate"/></a>
					</div>
				</div>
			</div>
		</form>
	</div>
	
	<display:table name="cultureList" class="table table-condensed table-striped table-hover" requestURI="" id="cultureList" export="true" pagesize="25">
	    <display:column property="id" sortable="true" href="${ctx}/i18n/cultureform" media="html"
	        paramId="id" paramProperty="id" titleKey="culture.id"/>
	    <display:column property="id" media="csv excel xml pdf" titleKey="culture.id"/>
	    <display:column property="cultureCode" sortable="true" titleKey="culture.cultureCode"/>
	    <display:column property="cultureName" sortable="true" titleKey="culture.cultureName"/>
	
	    <display:setProperty name="paging.banner.item_name"><fmt:message key="cultureList.culture"/></display:setProperty>
	    <display:setProperty name="paging.banner.items_name"><fmt:message key="cultureList.cultures"/></display:setProperty>
	
	    <display:setProperty name="export.excel.filename"><fmt:message key="cultureList.title"/>.xls</display:setProperty>
	    <display:setProperty name="export.csv.filename"><fmt:message key="cultureList.title"/>.csv</display:setProperty>
	    <display:setProperty name="export.pdf.filename"><fmt:message key="cultureList.title"/>.pdf</display:setProperty>
	</display:table>
</div>
