<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="cultureList.title"/></title>
    <meta name="menu" content="CultureMenu"/>
</head>

<c:set var="group" value="grp_boostrap_table_treegrid" scope="request" />
<c:set var="scripts" scope="request">
    <%@ include file="/static/scripts/i18n/culture.js"%>
</c:set>

<div class="col">
	<c:if test="{'$'}{not empty searchError}">
	    <div class="alert alert-danger alert-dismissable">
	        <a href="#" data-dismiss="alert" class="close">&times;</a>
	        <c:out value="{'$'}{searchError}"/>
	    </div>
	</c:if>
	
	<h2><fmt:message key="cultureList.heading"/></h2>
		
	<form id="searchForm" class="form-inline float-end">
        <div id="search" class="input-group">
             <input type="text" size="20" name="q" id="query" value="${param.q}"
                       placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
            <span class="input-group-append">
                <button id="buttonSearch" class="btn btn-default btn-sm">
                    <i class="fa fa-search"></i> <fmt:message key="button.search"/>
                </button>
            </span>
        </div>
    </form>
	
	<p><fmt:message key="cultureList.message"/></p>
		
	<div id="actions" class="btn-group">
	    <secure:hasPermission name="i18n:cultures:add">
	        <a href='<c:url value="/i18n/cultureform"/>' class="btn btn-primary">
	            <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
	    </secure:hasPermission>
	    <secure:hasPermission name="i18n:cultures:delete">
	        <button class="btn btn-danger" id="delete" name="delete">
	            <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
	    </secure:hasPermission>
	    <secure:hasPermission name="i18n:cultures:data_import">
	        <button id="btnImport" class="btn btn-success" type="button" title="<fmt:message key="button.import"/>">
	            <i class="fa fa-file-import"></i> <span class="hidden-xs"><fmt:message key="button.import"/></span></button>
	    </secure:hasPermission>
	    <secure:hasPermission name="i18n:cultures:data_export">
	        <button id="btnExport" class="btn btn-info" type="button" title="<fmt:message key="button.export"/>">
	            <i class="fa fa-file-export"></i><span class="hidden-xs"><fmt:message key="button.export"/></span></button>
	    </secure:hasPermission>
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
		
	<div class="table-responsive">
        <table id="table">
            <thead>
            <tr>
                <th data-field="state" data-checkbox="true"></th>
                <th data-field="cultureCode" data-formatter="nameFormatter"><fmt:message key="culture.cultureCode"/></th>
                <th data-field="cultureName"><fmt:message key="culture.cultureName"/></th>
            </tr>
            </thead>
        </table>
    </div>
</div>
