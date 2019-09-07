<%@ include file="/common/taglibs.jsp"%>

<%-- <head>
	<title><fmt:message key="albumtree.title"/></title>
</head>

<c:set var="group" value="grp_layout" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/cm/albums/albumTreeView.js"%>
</c:set> --%>

<%-- <input type="hidden" name="galleryId" value="<c:out value="${param.galleryId}"/>"/>
<input type="hidden" name="albumId" value="<c:out value="${param.albumId}"/>"/> --%>
<input type="hidden" id="hdnCheckedAlbumIds" name="hdnCheckedAlbumIds"/>
<div id="albumTreeView" >loding... ...</div>