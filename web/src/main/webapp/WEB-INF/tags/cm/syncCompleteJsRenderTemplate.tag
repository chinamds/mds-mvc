<%@ tag language="java" pageEncoding="UTF-8"%>
<%-- <%@ include file="/common/tagcommlibs.jsp"%> --%>
<%@ tag import="com.mds.common.Constants" %>
<%@ tag import="com.mds.util.StringUtils" %>
<%@ tag import="com.mds.i18n.util.I18nUtils" %>

 <%
 	//out.write(getthumbnailTemplates());
 	if (request.getAttribute("scripts") == null){
		request.setAttribute("scripts", getSyncCompleteJsRenderTemplate(request));
	}else{
		request.setAttribute("scripts", (request.getAttribute("scripts") + "\n" + getSyncCompleteJsRenderTemplate(request)));
	}
 %>
 
<%!
protected String getSyncCompleteJsRenderTemplate(HttpServletRequest request)  {
    String taskSynchProgressSkippedObjectsMaxExceededMsg = I18nUtils.getString("task.synch.Progress_Skipped_Objects_Max_Exceeded_Msg", request.getLocale(), Constants.MaxNumberOfSkippedObjectsToDisplayAfterSynch);

    return StringUtils.format(
"<script id='tmplSyncSkippedFiles' type='text/x-jsrender'>\n" +     		
"	<p>{0}</p>\n" +
"	{{if SkippedFiles.length > 0}}\n" +
"	{{if SkippedFiles.length >= {1}}}\n" +
"	  <p class='mds_msgwarning_o'>{2}</p>\n" +
"	{{else}}\n" +
"	  <p class='mds_msgwarning_o'>{3}</p>\n" +
"	{{/if}}\n" +
"	<ul class='mds_sync_sts_sf_ctr mds_fs'>\n" +
"	{{for SkippedFiles}}\n" +
"	  {{if #index < {1}}}\n" +
"	    <li><span class='mds_sync_sts_sf'>{{>Key}}:</span>&nbsp;<span class='mds_sync_sts_sf_v'>{{>Value}}</span></li>\n" +
"	  {{/if}}\n" +
"	{{/for}}\n" +
"	</ul>\n" +
"	<p class='mds_msgfriendly_o mds_fs'>{4}</p>\n" +
"	{{/if}}\n" + 
"</script>\n"			
,
	I18nUtils.getString("task.synch.Progress_Successful", request.getLocale()), // 0
	Constants.MaxNumberOfSkippedObjectsToDisplayAfterSynch, // 1
	taskSynchProgressSkippedObjectsMaxExceededMsg, // 2
	I18nUtils.getString("task.synch.Progress_Skipped_Objects_Msg1", request.getLocale()), // 3
	I18nUtils.getString("task.synch.Progress_Skipped_Objects_Msg2", request.getLocale()) // 4
	);
}
%>