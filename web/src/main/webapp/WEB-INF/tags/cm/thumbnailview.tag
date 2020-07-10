<%@ tag language="java" pageEncoding="UTF-8"%>
<%-- <%@ include file="/common/tagcommlibs.jsp"%> --%>
<%@ tag import="com.mds.cm.content.UiTemplateBo" %>
<%@ tag import="com.mds.cm.content.AlbumBo" %>
<%@ tag import="com.mds.core.UiTemplateType" %>
<%@ tag import="com.mds.util.StringUtils" %>
<%@ tag import="com.mds.cm.util.CMUtils" %>

<%@ attribute name="album" type="com.mds.cm.content.AlbumBo" required="true" description="album folder" %>
<%-- <%@ attribute name="uiTemplate" type="com.mds.cm.content.UiTemplateBo" required="true" description="ui template" %> --%>
<%@ attribute name="id" type="java.lang.String" required="true"%>
<%@ attribute name="mdsClientId" type="java.lang.String" required="true"%>
<%@ attribute name="thumbnailHtmlTmplId" type="java.lang.String" required="true"%>
<%@ attribute name="thumbnailScriptTmplId" type="java.lang.String" required="true"%>
<%@ attribute name="thumbnailTmplName" type="java.lang.String" required="true"%>

 <div id='${id}'></div>
 <%
 	//out.write(getthumbnailTemplates());
 	if (request.getAttribute("scripts") == null){
		request.setAttribute("scripts", getthumbnailTemplates());
	}else{
		request.setAttribute("scripts", (request.getAttribute("scripts") + "\n" + getthumbnailTemplates()));
	}
 %>
 
<%!
private String getthumbnailTemplates(){
	UiTemplateBo uiTemplate = CMUtils.loadUiTemplates().get(UiTemplateType.Album, album);
	
	//String script = StringUtils.format(
	return StringUtils.format(
"<script id='{0}' type='text/x-jsrender'>\n" +
"{1}\n" + 
"</script>\n" +
"<script id='{2}' type='text/x-jsrender'>\n" +
"{3}\n" +
"</script>\n" +
"<script>\n" +
"(function ($) {\n" +
"	$(document).ready(function () {\n" +
"		$.templates({{5}: $('#{0}').html() });\n" +
"		(new Function($('#{2}').render(window.{4}.mdsData)))();\n" +
"	});\n" +
"})(jQuery);\n" +
"</script>\n"
,
		thumbnailHtmlTmplId, // 0
		uiTemplate.HtmlTemplate, //StringUtils.replace(StringUtils.replace(uiTemplate.HtmlTemplate, "{{", "{"), "}}", "}"), // 1
		thumbnailScriptTmplId, // 2
		uiTemplate.ScriptTemplate, //StringUtils.replace(StringUtils.replace(uiTemplate.ScriptTemplate, "{{", "{"), "}}", "}"), // 3
		mdsClientId, // 4
		thumbnailTmplName // 5
	);
}
%>