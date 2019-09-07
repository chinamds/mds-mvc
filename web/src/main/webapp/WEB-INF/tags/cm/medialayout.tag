<%@ tag language="java" pageEncoding="UTF-8"%>
<%-- <%@ include file="/common/tagcommlibs.jsp"%> --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ tag import="com.mds.cm.content.UiTemplateBo" %>
<%@ tag import="com.mds.core.UiTemplateType" %>
<%@ tag import="com.mds.util.StringUtils" %>
<%@ tag import="com.mds.cm.util.CMUtils" %>

<%@ attribute name="album" type="com.mds.cm.content.AlbumBo" required="true" description="album folder" %>
<%@ attribute name="id" type="java.lang.String" required="true"%>
<%@ attribute name="mdsClientId" type="java.lang.String" required="true"%>
<%@ attribute name="leftPaneVisible" type="java.lang.Boolean" required="true"%>
<%@ attribute name="leftPaneDocked" type="java.lang.Boolean" required="false"%>
<%@ attribute name="centerPaneVisible" type="java.lang.Boolean" required="true"%>
<%@ attribute name="rightPaneVisible" type="java.lang.Boolean" required="true"%>
<%@ attribute name="rightPaneDocked" type="java.lang.Boolean" required="false"%>

<%@ attribute name="leftPaneHtmlTmplClientId" type="java.lang.String" required="true"%>
<%@ attribute name="leftPaneScriptTmplClientId" type="java.lang.String" required="true"%>
<%@ attribute name="leftPaneTmplName" type="java.lang.String" required="true"%>

<%@ attribute name="rightPaneHtmlTmplClientId" type="java.lang.String" required="true"%>
<%@ attribute name="rightPaneScriptTmplClientId" type="java.lang.String" required="true"%>
<%@ attribute name="rightPaneTmplName" type="java.lang.String" required="true"%>

<%@ attribute name="allPanesContainerClientID" type="java.lang.String" required="true"%>
<%@ attribute name="centerAndRightPanesContainerClientID" type="java.lang.String" required="true"%>


<c:if test="${empty leftPaneDocked}"><c:set var="leftPaneDocked" value="false"/></c:if>
<c:if test="${empty rightPaneDocked}"><c:set var="rightPaneDocked" value="false"/></c:if>

 <% 
 //registerJavaScript(request);
//Add left and right pane templates, then invoke their scripts.
	// Note that when the header is visible, we wait for it to finish rendering before running our script.
	// We do this so  that the splitter's height calculations are correct.
	// When the device is a touchscreen, we double the width of the splitter pane (from 6px to 12px).
	// We trigger a javascript event mdsPanesRendered so that any dependent code that queries the final width
	// and height of the panes can run (not currently used anywhere but could be useful to UI template editors).
	String script = StringUtils.format(
"{0}\n" + 
"{1}\n" +
"<script>\n" +
"$(document).ready(function () {\n" +
"var runPaneScripts = function() {\n" +
"	{2}" +
"	{3}" +
"	{4}" +
"	{6}" +
"	" +
"	$(document.documentElement).trigger('mdsPanesRendered.{5}');\n" +
"};\n" +
"   \n" +
"if (window.{5}.mdsData.Settings.ShowHeader)\n" +
"	$(document.documentElement).on('mdsHeaderLoaded.{5}', runPaneScripts);\n" +
"else\n" +
"	runPaneScripts();\n" +
"});\n" +
"</script>\n"
,
								GetLeftPaneTemplates(), // 0
								GetRightPaneTemplates(), // 1
								GetLeftPaneScript(), // 2
								GetRightPaneScript(), // 3
								GetCenterPaneScript(), // 4
		                        mdsClientId, // 5
		                        GetTouchScreenHacks(request) // 6
		                        );
	//out.write(script);
	if (request.getAttribute("scripts") == null){
		request.setAttribute("scripts", script);
	}else{
		request.setAttribute("scripts", (request.getAttribute("scripts") + "\n" + script));
	}
 %>
 
<%!
/* private void registerJavaScript(HttpServletRequest request){
	// Add left and right pane templates, then invoke their scripts.
	// Note that when the header is visible, we wait for it to finish rendering before running our script.
	// We do this so  that the splitter's height calculations are correct.
	// When the device is a touchscreen, we double the width of the splitter pane (from 6px to 12px).
	// We trigger a javascript event mdsPanesRendered so that any dependent code that queries the final width
	// and height of the panes can run (not currently used anywhere but could be useful to UI template editors).
	String script = StringUtils.format(
"{0}" + 
"{1}" +
"<script>" +
"$().ready(function () {{" +
"var runPaneScripts = function() {{" +
"	{2}" +
"	{3}" +
"	{4}" +
"	{6}" +
"	" +
"	$(document.documentElement).trigger('mdsPanesRendered.{5}');" +
"}};" +
"   " +
"if (window.{5}.mdsData.Settings.ShowHeader)" +
"	$(document.documentElement).on('mdsHeaderLoaded.{5}', runPaneScripts);" +
"else" +
"	runPaneScripts();" +
"}});" +
"</script>"
,
		                        GetLeftPaneTemplates(), // 0
		                        GetRightPaneTemplates(), // 1
		                        GetLeftPaneScript(), // 2
		                        GetRightPaneScript(), // 3
		                        GetCenterPaneScript(), // 4
		                        mdsClientId, // 5
		                        GetTouchScreenHacks(request) // 6
		                        );
} */

/// <summary>
/// Gets some JavaScript to make touchscreen devices work better.
/// </summary>
/// <returns>System.String.</returns>
private static String GetTouchScreenHacks(HttpServletRequest request){
	// Implement these rules if a touchscreen less than 1500px wide is detected:
	// 1. Increase width of splitter bar to 12px.
	// 2. For non-IE browsers:
	//    (a) Remove scrollbars in center pane (necessary because Safari/Chrome has hidden scrollbars on 
	//        small devices that can't be selected, and the Selectable in the center pane prevents scrolling.
	//    (b) Make splitter bars draggable, which activates the Touch Punch hack
	String  browserDetails  =   request.getHeader("User-Agent");        
	String  userAgent       =   browserDetails;        
	String  user            =   userAgent.toLowerCase();

	boolean isIe = user.contains("msie");
	
	final String nonIeScript = 
"$('.mds_tb_s_CenterPane').css('overflow', 'visible');\n" + 
"$('.splitter-bar').draggable();\n";

	return StringUtils.format(
"if (window.Mds.isTouchScreen() && window.Mds.isWidthLessThan(1500)) {\n" + 
"$('.splitter-bar-vertical').width('12');{0}\n" + 
"}",
		    isIe ? StringUtils.EMPTY : nonIeScript);
}

private String GetLeftPaneTemplates(){
	UiTemplateBo uiTemplate = CMUtils.loadUiTemplates().get(UiTemplateType.LeftPane, album);

	return StringUtils.format(
"<script id='{0}' type='text/x-jsrender'>\n" + 
"{1}\n" + 
"</script>\n" + 
"<script id='{2}' type='text/x-jsrender'>\n" + 
"{3}\n" + 
"</script>\n"  
,
		leftPaneHtmlTmplClientId, // 0
		uiTemplate.HtmlTemplate, // 1
		leftPaneScriptTmplClientId, // 2
		uiTemplate.ScriptTemplate // 3
        );
}

private String GetRightPaneTemplates(){
	UiTemplateBo uiTemplate = CMUtils.loadUiTemplates().get(UiTemplateType.RightPane, album);

	return StringUtils.format(
"<script id='{0}' type='text/x-jsrender'>\n" + 
"{1}\n" + 
"</script>\n" + 
"<script id='{2}' type='text/x-jsrender'>\n" + 
"{3}\n" + 
"</script>\n" 
,
		rightPaneHtmlTmplClientId, // 0
		uiTemplate.HtmlTemplate, // 1
		rightPaneScriptTmplClientId, // 2
		uiTemplate.ScriptTemplate // 3
        );
}

private String GetLeftPaneScript(){
	if (!leftPaneVisible)
		return StringUtils.EMPTY;

	// Call splitter jQuery plug-in that sets up the split between the left and center panes
	// The splitter is only called when the gallery control's width is greater than 750px, because
	// we don't want it on small media screens (like smart phones)
	return StringUtils.format(
"$.templates({{0}: $('#{1}').html() });\n" + 
"(new Function($('#{2}').render(window.{3}.mdsData)))();\n" + 
"{4}\n" 
,
			leftPaneTmplName, // 0
			leftPaneHtmlTmplClientId, // 1
			leftPaneScriptTmplClientId, // 2
			mdsClientId, // 3
			GetLeftPaneSplitterScript() // 4
			);
}

private String GetLeftPaneSplitterScript(){
	if (!centerPaneVisible)
		return StringUtils.EMPTY;

	// Call splitter jQuery plug-in that sets up the split between the left and center panes
	// The splitter is only called when the gallery control's width is greater than 750px, 
	// because we don't want it on small media screens (like smart phones).
	return StringUtils.format(
"if ($('#{0}').width() >= 750) {\n" + 
"$('#{1}').splitter({\n" + 
"type: 'v',\n" + 
"outline: false,\n" + 
"minLeft: 100, sizeLeft: {2}, maxLeft: 600,\n" + 
"dock: 'left',\n" + 
"dockSpeed: 200,\n" + 
"anchorToWindow: true,\n" + 
"accessKey: 'L',\n" + 
"splitbarClass: 'mds_vsplitbar',\n" + 
"cookie: 'mds_left-pane_{1}',\n" + 
"cookiePath: '/'\n" + 
"});\n" + 
"}\n" 
,
			mdsClientId, // 0
			allPanesContainerClientID, // 1
			leftPaneDocked ? "0" : "true" // 2
			);
}

private String GetRightPaneScript(){
	if (!rightPaneVisible)
		return StringUtils.EMPTY;

	// Call splitter jQuery plug-in that sets up the split between the center and right panes.
	// The splitter is only called when the gallery control's width is greater than 750px, because
	// we don't want it on small media screens (like smart phones)
	return StringUtils.format(
"$.templates({{0}: $('#{1}').html() });\n" + 
"(new Function($('#{2}').render(window.{3}.mdsData)))();\n" + 
"\n" + 
"{4}\n" 
,
	    rightPaneTmplName, // 0
	    rightPaneHtmlTmplClientId, // 1
	    rightPaneScriptTmplClientId, // 2
	    mdsClientId, // 3
	    GetRightPaneSplitterScript()
	    );
}

private String GetCenterPaneScript(){
	if ((leftPaneVisible && rightPaneVisible) || !centerPaneVisible)
		return StringUtils.EMPTY;

	// When either the left pane or right pane is hidden, we no longer want overflow:auto applied to
	// the center pane.
	return StringUtils.format("$('.mds_tb_s_CenterPane', $('#{0}')).css('overflow', 'inherit');",
		mdsClientId);
}

private String GetRightPaneSplitterScript(){
	// Call splitter jQuery plug-in that sets up the split between the center and right panes.
	// The splitter is only called when the gallery control's width is greater than 750px, 
	// because we don't want it on small media screens (like smart phones).
	if (!centerPaneVisible)
		return StringUtils.EMPTY;

	return StringUtils.format(
"if ($('#{0}').width() >= 750) {\n" + 
"$('#{1}').splitter({\n" + 
"type: 'v',\n" + 
"outline: false,\n" + 
"minRight: 100, sizeRight: {2}, maxRight: 1000,\n" + 
"dock: 'right',\n" + 
"dockSpeed: 200{3},\n" + 
"accessKey: 'R',\n" + 
"splitbarClass: 'mds_vsplitbar',\n" + 
"cookie: 'mds_right-pane_{1}',\n" + 
"cookiePath: '/'\n" + 
"});\n" + 
"}\n"  
,
	    mdsClientId, // 0
	    centerAndRightPanesContainerClientID, // 1
	    rightPaneDocked ? "0" : "true", // 2
	    leftPaneVisible ? StringUtils.EMPTY : ",anchorToWindow: true" // 3
	    );
}
%>