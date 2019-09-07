<%@ tag language="java" pageEncoding="UTF-8"%>
<%-- <%@ include file="/common/tagcommlibs.jsp"%> --%>
<%@ tag import="com.mds.util.StringUtils" %>
<%@ tag import="com.mds.cm.util.AlbumTreePickerBuilder" %>

<%@ attribute name="galleryView" type="com.mds.cm.util.GalleryView" required="true" description="gallery View customize" %>
<%@ attribute name="multiRotate" type="java.lang.Boolean" required="false"%>

<%
	AlbumTreePickerBuilder rotateImageHtmlBuilder = new AlbumTreePickerBuilder();
	if (multiRotate != null && multiRotate == true){
		out.write(rotateImageHtmlBuilder.getRotatesHtml(galleryView, request));
	}else{
		out.write(rotateImageHtmlBuilder.getRotateHtml(galleryView, galleryView.getContentObject(), false, request));
	}
%>
