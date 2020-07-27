<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.mds.aiotplayer.core.ResourceId" %>
<%@ page import="com.mds.aiotplayer.cm.content.UiTemplateBo" %>
<%@ page import="com.mds.aiotplayer.util.StringUtils" %>


<head>
	<title><fmt:message key="galleryList.title"/></title>
	<meta name="_csrf" content="${_csrf.token}"/>
    <!-- default header name is X-CSRF-TOKEN -->
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <meta name="menu" content="Repository"/>   
	<meta name="container" content="galleryView"/>
	<meta name="mdsClientId" content="${galleryView.mdsClientId}"/>
	<meta name="mdsShowAlbumMenu" content="${galleryView.showAlbumMenu}"/>
</head>

<c:set var="group" value="grp_layout" scope="request" />
<c:set var="scripts" scope="request">
	<%@ include file="/static/scripts/cm/main.js"%>
</c:set>

<c:set var="leftPaneVisible" value="true"/>
<c:set var="showLeftPaneForAlbum" value="${galleryView.showLeftPaneForAlbum}"/>
<c:set var="showLeftPaneForContentObject" value="${galleryView.showLeftPaneForContentObject}"/>
<c:set var="showCenterPane" value="${galleryView.showCenterPane}"/>
<c:set var="showRightPane" value="${galleryView.showRightPane}"/>

<c:set var="mdsClientId" value="${galleryView.mdsClientId}"/>
<c:set var="leftPaneClientId" value="${galleryView.leftPaneClientId}"/>
<c:set var="leftPaneTmplName" value="${galleryView.leftPaneTmplName}"/>
<c:set var="rightPaneClientId" value="${galleryView.rightPaneClientId}"/>
<c:set var="rightPaneTmplName" value="${galleryView.rightPaneTmplName}"/>
<c:if test="${resourceId eq ResourceId.album}">
    <c:set var="leftPaneVisible" value="${galleryView.showLeftPaneForAlbum}"/>
</c:if>
<c:if test="${resourceId eq ResourceId.contentobject}">
    <c:set var="leftPaneVisible" value="${galleryView.showLeftPaneForContentObject}"/>
</c:if>
	
<cm:data id="mdsData" albumTreeData="${albumTreeData}" mdsClientId="${mdsClientId}" showLeftPaneForAlbum="${showLeftPaneForAlbum}" showLeftPaneForContentObject="${showLeftPaneForContentObject}" galleryView="${galleryView}"/>

<cm:medialayout album="${galleryView.album}" id="mdsMedia" mdsClientId="${mdsClientId}" leftPaneVisible="${leftPaneVisible}" leftPaneDocked="false"
	centerPaneVisible="${showCenterPane}" rightPaneVisible="${showRightPane}" rightPaneDocked="false" 
	leftPaneHtmlTmplClientId="mds_lpHtmlTmpl" leftPaneScriptTmplClientId="mds_lpScriptTmpl" leftPaneTmplName="${leftPaneTmplName}" rightPaneHtmlTmplClientId="mds_rpHtmlTmpl" 
	rightPaneScriptTmplClientId="mds_rpScriptTmpl" rightPaneTmplName="${rightPaneTmplName}"	allPanesContainerClientID="media" centerAndRightPanesContainerClientID="mediaCR"/> 
 <c:choose>
 	<c:when test="${leftPaneVisible}">
		<div id="media" class="mds_s_c">
			<div id="${leftPaneClientId}" class="mds_tb_s_LeftPane mds_tb_s_pane"></div>
				<div id="mediaCR" class="mds_tb_s_CenterAndRightPane">
				<c:if test="${showCenterPane}">
					<div class="mds_tb_s_CenterPane mds_tb_s_pane">
						<c:if test="${resourceId eq ResourceId.album}">
							<%@include file="./contentobjects/thumbnailview.jsp"%>
						</c:if>
						<c:if test="${resourceId eq ResourceId.contentobject}">
							<%@include file="./contentobjects/mediaview.jsp"%>
						</c:if>
					</div>
				</c:if>
				<c:if test="${showRightPane}">
					<div id="${rightPaneClientId}" class="mds_tb_s_RightPane mds_tb_s_pane"></div>
				</c:if>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div id="mediaCR" class="mds_tb_s_CenterAndRightPane">
			<c:if test="${ShowCenterPane}">
				<div class="mds_tb_s_CenterPane mds_tb_s_pane">
					<c:if test="${resourceId eq ResourceId.album}">
						<%@include file="./contentobjects/thumbnailview.jsp"%>
					</c:if>
					<c:if test="${resourceId eq ResourceId.contentobject}">
						<%@include file="./contentobjects/mediaview.jsp"%>
					</c:if>
				</div>
			</c:if>
			<c:if test="${showRightPane}">
				<div id="${rightPaneClientId}" class="mds_tb_s_RightPane mds_tb_s_pane"></div>
			</c:if>
		</div>
	</c:otherwise>
</c:choose>