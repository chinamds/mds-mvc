<%@ include file="/common/taglibs.jsp"%>

<page:applyDecorator name="default_dlg">
<head>
	<title><fmt:message key="albumtreepicker.title"/></title>
</head>

<c:set var="group" value="grp_appendgrid" scope="request" />
<%-- <c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/cm/albumtreepicker.js"%>
</c:set> --%>
<body class="mds-bootstrap-dialog">
<div class="albumtreepicker col-sm-10">
	<cm:albumtreepicker id="album" keyName="albumId" keyValue="" fieldName="albumName" fieldValue=""
		cssClass="required form-control" maxlength="100" allowMultiCheck="false" treeViewTheme="default" requiredSecurityPermissions="${requiredSecurityPermissions}" galleryId="${galleryId}"
	    enableCheckboxPlugin="false" requireAlbumSelection="true" selectedAlbumIds="${selectedAlbumIds}"/>
</div>
</body>   
</page:applyDecorator>