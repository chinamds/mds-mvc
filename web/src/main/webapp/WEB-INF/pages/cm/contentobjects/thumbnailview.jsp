<%@ include file="/common/taglibs.jsp"%>

<%-- <head>
	<title><fmt:message key="mediaView.title"/></title>
</head> --%>

<%-- <c:set var="group" value="grp_gallery" scope="request" /> --%>
<%-- <c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/cm/albums/albumTreeView.js"%>
</c:set> --%>

<%-- <input type="hidden" name="galleryId" value="<c:out value="${param.galleryId}"/>"/>
<input type="hidden" name="albumId" value="<c:out value="${param.aid}"/>"/> --%>
<!-- <div id="mediaView" >mediaView</div>  uiTemplate="${uiTemplate}" -->
<cm:thumbnailview album="${album}" id="${mdsClientId}_thmbHtml" mdsClientId="${mdsClientId}" thumbnailHtmlTmplId="${mdsClientId}_thmbHtmlTmpl" thumbnailScriptTmplId="${mdsClientId}_thmbScriptTmpl" thumbnailTmplName="${mdsClientId}_thumbnail_tmpl"/>