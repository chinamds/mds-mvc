<%@ tag language="java" pageEncoding="UTF-8"%>
<%-- <%@ include file="/common/tagcommlibs.jsp"%> --%>
<%@ tag import="com.mds.aiotplayer.util.StringUtils" %>
<%@ tag import="com.mds.aiotplayer.cm.util.AlbumTreePickerBuilder" %>

<%@ attribute name="galleryView" type="com.mds.aiotplayer.cm.util.GalleryView" required="true" description="gallery View customize" %>
<%@ attribute name="multiRotate" type="java.lang.Boolean" required="false"%>

<%
	AlbumTreePickerBuilder rotateImageHtmlBuilder = new AlbumTreePickerBuilder();
	if (multiRotate != null && multiRotate == true){
		out.write(rotateImageHtmlBuilder.getRotatesHtml(galleryView, request));
	}else{
		out.write(rotateImageHtmlBuilder.getRotateHtml(galleryView, galleryView.getContentObject(), false, request));
	}
%>
