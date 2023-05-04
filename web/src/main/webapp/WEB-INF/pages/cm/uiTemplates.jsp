<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="uiTemplateList.title"/></title>
    <meta name="menu" content="UiTemplateMenu"/>
</head>
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/cm/uiTemplate.js"%>
</c:set>

<div class="col">
	<c:if test="{'$'}{not empty searchError}">
	    <div class="alert alert-danger alert-dismissable">
	        <a href="#" data-bs-dismiss="alert" class="close">&times;</a>
	        <c:out value="{'$'}{searchError}"/>
	    </div>
	</c:if>
	
	<h2><fmt:message key="uiTemplateList.heading"/></h2>
	
	<%-- <form method="get" action="${ctx}/uiTemplates" id="searchForm" class="form-inline">  --%>
	<form id="searchForm" class="form-inline float-end">
	<div id="search" class="input-group">
		<input type="text" size="20" name="q" id="query" value="${param.q}" 
	               placeholder="<fmt:message key="search.enterTerms"/>" class="form-control input-sm"/>
		<button id="buttonSearch" class="btn btn btn-outline-primary btn-sm">
			<i class="fa fa-search"></i> <fmt:message key="button.search"/>
		</button>
	</div>
	</form>
	
	<p><fmt:message key="uiTemplateList.message"/></p>
	
	<div id="actions" class="btn-group">
		<secure:hasPermission name="cm:uiTemplates:add">
	    <a href='javascript:addAction();' class="btn btn-primary">
	        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
	    </secure:hasPermission>
	    <secure:hasPermission name="cm:uiTemplates:delete">
	    <button class="btn btn-danger" id="delete" name="delete">
	        <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
	    </secure:hasPermission>
	    <secure:hasPermission name="cm:uiTemplates:data_import">
	    <button id="btnImport" class="btn btn-success" type="button" title="<fmt:message key="button.import"/>">
	        <i class="fa fa-file-import"></i> <span class="hidden-xs"><fmt:message key="button.import"/></span></button>
	    </secure:hasPermission>
	    <secure:hasPermission name="cm:uiTemplates:data_export">
	    <button id="btnExport" class="btn btn-info" type="button" title="<fmt:message key="button.export"/>">
	        <i class="fa fa-file-export"></i><span class="hidden-xs"><fmt:message key="button.export"/></span></button>
	    </secure:hasPermission>
	    <a href='<c:url value="/home"/>' class="btn btn-default d-md-none d-xs-block d-sm-block"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
	</div>
	
	<div id="importBox" class="hidden d-none">
		<form id="importForm" method="post" enctype="multipart/form-data"
			class="form-horizontal"><br/>
			<div class="mb-3">
				<label class="control-label col-sm-3"><fmt:message key="import.file"/></label>
				<div class="col-sm-9">
					<input name="importFile" id="uploadFile" type="file" class="file" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel">
					<p class="help-block"><fmt:message key="import.information" /></p><br/><br/>
				</div>
		        <div class="mb-3">
	            	<div class="col-sm-9 col-sm-offset-3">
						<a href="${ctx}/cm/uiTemplates/import/template"><fmt:message key="button.downloadtemplate"/></a>
					</div>
				</div>
			</div>
		</form>
	</div>
	
	<div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasAdd" data-bs-backdrop="static" aria-labelledby="offcanvasAddLabel">
      <div class="offcanvas-header py-2">
          <h3 class="offcanvas-title text-primary fw-bolder" id="offcanvasAddLabel"></h3>
          <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
      </div>
      <div class="offcanvas-body">
            <iframe id="uiTemplateAddFrame" name="uiTemplateAddFrame" width="100%" height="100%"  frameborder="0" scrolling="auto"
                            src="<c:url value="/cm/uiTemplateform"/>"></iframe>
      </div>
    </div>
    
    <div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasEdit" data-bs-backdrop="false" aria-labelledby="offcanvasEditLabel">
      <div class="offcanvas-header py-2">
          <h3 class="offcanvas-title text-primary fw-bolder" id="offcanvasEditLabel"></h3>
          <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
      </div>
      <div class="offcanvas-body">
            <iframe id="uiTemplateEditFrame" name="uiTemplateEditFrame" width="100%" height="100%"  frameborder="0" scrolling="auto"
                            src="<c:url value="/cm/uiTemplateform"/>"></iframe>
      </div>
    </div>
	
	<div class="table-responsive">
		<table id="table">
	            <thead>
	            <tr>
	                <th data-field="state" data-checkbox="true" data-formatter="stateFormatter"></th>
	                <th data-field="id"
	                    data-align="center"
	                    data-formatter="actionFormatter"
	                    data-events="actionEvents"><fmt:message key="table.operation"/></th>
	                <th data-field="name"><fmt:message key="uiTemplate.name"/></th>
	                <th data-field="templateType"><fmt:message key="uiTemplate.templateType"/></th>
	                <th data-field="description"><fmt:message key="uiTemplate.description"/></th>               
                    <th data-field="galleryName"><fmt:message key="uiTemplate.gallery"/></th>   		        	
	            </tr>
	            </thead>
	        </table>
	</div>
</div>