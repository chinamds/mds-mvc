<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="galleryList.title"/></title>
    <meta name="menu" content="GalleryMenu"/>
</head>

<c:set var="group" value="grp_form" scope="request" />
<c:set var="scripts" scope="request">
<%@ include file="/static/scripts/cm/gallery.js"%>
</c:set>

<c:if test="{'$'}{not empty searchError}">
    <div class="alert alert-danger alert-dismissable">
        <a href="#" data-dismiss="alert" class="close">&times;</a>
        <c:out value="{'$'}{searchError}"/>
    </div>
</c:if>

<div class="col">
<h2><fmt:message key="galleryList.heading"/></h2>

<%@include file="../common/searchbox.jsp"%>

<p><fmt:message key="galleryList.message"/></p>

<div id="actions" class="btn-group">
	<secure:hasPermission name="cm:galleries:add">
    <a href='<c:url value="/cm/galleryform"/>' class="btn btn-primary">
        <i class="fa fa-plus icon-white"></i> <fmt:message key="button.add"/></a>
    </secure:hasPermission>
    <secure:hasPermission name="cm:galleries:delete">
    <button class="btn btn-danger" id="delete" name="delete">
        <i class="fa fa-trash icon-white"></i> <fmt:message key="button.delete"/></button>
    </secure:hasPermission>
    <secure:hasPermission name="cm:galleries:data_import">
    <button id="btnImport" class="btn btn-success hidden-xs" type="button" title="<fmt:message key="button.import"/>">
        <i class="fa fa-file-import"></i><fmt:message key="button.import"/></button>
    </secure:hasPermission>
    <secure:hasPermission name="cm:galleries:data_export">
    <button id="btnExport" class="btn btn-info hidden-xs" type="button" title="<fmt:message key="button.export"/>">
        <i class="fa fa-file-export"></i><fmt:message key="button.export"/></button>
    </secure:hasPermission>
    <secure:hasAnyPermissions name="cm:galleries:data_import,cm:galleries:data_export">
    <div class="btn-group visible-xs-block visible-sm-block">
	    <button type="button" class="btn btn-info dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
		    <fmt:message key="button.moreactions"/> <span class="caret"></span></button>
		<ul class="dropdown-menu">
		  <li><a class="btn-moreactions-import"><i class="fa fa-file-import"></i>&nbsp;<fmt:message key="button.import"/></a></li>
		  <li><a class="btn-moreactions-export"><i class="fa fa-file-export"></i>&nbsp;<fmt:message key="button.export"/></a></li>
		</ul>
	</div>
	</secure:hasAnyPermissions>
    <a href='<c:url value="/home"/>' class="btn btn-default visible-xs-block visible-sm-block d-md-none d-xs-block d-sm-block"><i class="fa fa-check"></i> <fmt:message key="button.done"/></a>
</div>

<div id="importBox" class="hidden d-none">
	<form id="importForm" action="${ctx}/cm/galleries/import" method="post" enctype="multipart/form-data"
		style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('<fmt:message key="importform.importing"/>');"><br/>
		<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>
		<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="<fmt:message key="button.import"/>"/>
		<a href="${ctx}/cm/galleries/import/template"><fmt:message key="button.downloadtemplate"/></a>
	</form>
</div>

<div class="table-responsive">
	<table id="table">
        <thead>
        <tr>
            <th data-field="state" data-checkbox="true"></th>
            <th data-field="name" data-formatter="nameFormatter"><fmt:message key="gallery.name"/></th>
            <th data-field="organizations"><fmt:message key="gallery.organizations"/></th>
	        <th data-field="isTemplate" data-formatter="templateFormatter"><fmt:message key="gallery.isTemplate"/></th>          
	        <th data-field="description"><fmt:message key="gallery.description"/></th>
        </tr>
        </thead>
    </table>
</div>
</div>