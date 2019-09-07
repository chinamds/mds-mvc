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
<!-- <div id="mediaView" >mediaView</div> -->
<cm:mediaview album="${album}" id="${mdsClientId}_mediaHtml" mdsClientId="${mdsClientId}" contentHtmlTmplId="${mdsClientId}_mediaHtmlTmpl" contentScriptTmplId="${mdsClientId}_mediaScriptTmpl" contentTmplName="${mdsClientId}_media_tmpl"/>