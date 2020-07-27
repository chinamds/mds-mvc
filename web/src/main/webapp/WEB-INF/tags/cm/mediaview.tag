<%@ tag language="java" pageEncoding="UTF-8"%>
<%-- <%@ include file="/common/tagcommlibs.jsp"%> --%>
<%@ tag import="com.mds.aiotplayer.cm.content.UiTemplateBo" %>
<%@ tag import="com.mds.aiotplayer.cm.content.AlbumBo" %>
<%@ tag import="com.mds.aiotplayer.core.UiTemplateType" %>
<%@ tag import="com.mds.aiotplayer.util.StringUtils" %>
<%@ tag import="com.mds.aiotplayer.cm.util.CMUtils" %>

<%@ attribute name="album" type="com.mds.aiotplayer.cm.content.AlbumBo" required="true" description="album folder" %>
<%@ attribute name="id" type="java.lang.String" required="true"%>
<%@ attribute name="mdsClientId" type="java.lang.String" required="true"%>
<%@ attribute name="contentHtmlTmplId" type="java.lang.String" required="true"%>
<%@ attribute name="contentScriptTmplId" type="java.lang.String" required="true"%>
<%@ attribute name="contentTmplName" type="java.lang.String" required="true"%>

 <%-- <%=getMediaTemplate(request, uiTemplate, id, contentHtmlTmplId, contentScriptTmplId, contentTmplName)%> --%>
 <div id='${id}'></div>
<%
	UiTemplateBo uiTemplate = CMUtils.loadUiTemplates().get(UiTemplateType.ContentObject, album);
/* private static String getMediaTemplate(HttpServletRequest request, UiTemplateBo uiTemplate
		, String id, String contentHtmlTmplId, String contentScriptTmplId, String contentTmplName) { */
	
	// Define 3 script tags. The first two hold the HTML and javascript jsRender templates.
	// The last contains start script that does 2 things:
	// 1. Compile the jsRender template and run the javascript generated in the template
	// 2. Generate the JavaScript from the template and add to the page
	String tempFmt = 
	"<script id='{0}' type='text/x-jsrender'>\n" + 
	"{1}\n" +
	"</script>\n" +
	"<script id='{2}' type='text/x-jsrender'>\n" + 
	"{3}\n" +
	"</script>\n" +
	"<script>\n" +
	"(function ($) {\n" +
	"$(document).ready(function () {\n" +
	"$.templates({{5}: $('#{0}').html() });\n" +
	"(new Function($('#{2}').render(window.{4}.mdsData)))();\n" +
	"});\n" +
	"})(jQuery);\n" +
	"</script>\n";
	//,
	
	String script =  StringUtils.format(tempFmt,
		contentHtmlTmplId, // 0
		uiTemplate.HtmlTemplate, // 1
		contentScriptTmplId, // 2
		uiTemplate.ScriptTemplate, // 3
		mdsClientId, // 4
		contentTmplName // 5
		); 

	//out.write(script);
	if (request.getAttribute("scripts") == null){
		request.setAttribute("scripts", script);
	}else{
		request.setAttribute("scripts", (request.getAttribute("scripts") + "\n" + script));
	}
/* } */
%>