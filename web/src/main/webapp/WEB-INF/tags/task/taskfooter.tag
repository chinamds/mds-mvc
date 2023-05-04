<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fns" uri="/WEB-INF/tlds/fns.tld" %>

<%@ tag import="com.mds.aiotplayer.common.Constants" %>
<%@ tag import="org.apache.commons.lang.StringUtils" %>

<%@ attribute name="task_Ok_Button_Text" type="java.lang.String" required="false" description="OK button text"%>
<%@ attribute name="task_Cancel_Button_Text" type="java.lang.String" required="false" description="Cancel button text"%>
<%@ attribute name="backURL" type="java.lang.String" required="false" description="Cancel button URL"%>
<%@ attribute name="task_Ok_Button_Tooltip" type="java.lang.String" required="false" description="OK button tooltip"%>
<%@ attribute name="task_Cancel_Button_Tooltip" type="java.lang.String" required="false" description="Cancel button tooltip"%>

<c:if test="${empty task_Ok_Button_Text}"><fmt:message key="task.default.Ok_Button_Text" var="task_Ok_Button_Text"/></c:if>
<c:if test="${empty task_Cancel_Button_Text}"><fmt:message key="task.default.Cancel_Button_Text" var="task_Cancel_Button_Text"/></c:if>
<%-- <c:if test="${empty task_Ok_Button_Tooltip}"><c:set var="task_Ok_Button_Tooltip" value=""/></c:if> --%>
<c:if test="${empty task_Cancel_Button_Tooltip}"><fmt:message key="task.default.Cancel_Button_Tooltip" var="task_Cancel_Button_Tooltip"/></c:if>

<div class="mds_rightBottom">
	<p class="mds_minimargin">
		<span class="mds_spinner_msg"></span>&nbsp;<img src="${fns:getSkinnedUrl(pageContext.request, '/images/wait-squares.gif')}" class="mds_spinner" alt=""/>&nbsp;<Button ID="btnOkBottom" type="button"
			class="btn btn-primary btn-sm mds_btnOkBottom${empty task_Ok_Button_Tooltip ? '' : ' title=\"' + task_Ok_Button_Tooltip + '\"'}"><i class="fa fa-check icon-white"></i> ${task_Ok_Button_Text}</button>
		<Button ID="btnCancelBottom"  type="button" onclick="javascript:window.location.href='<%=getBackURL(request)%>';"  title="${task_Cancel_Button_Tooltip}"
			class="btn btn-default btn-sm mds_btnCancelBottom"><i class="fa fa-times"></i> ${task_Cancel_Button_Text}</Button>&nbsp;</p>
</div>

<%!
	private String getBackURL(HttpServletRequest request) {
	    if(StringUtils.isBlank(backURL)) {
	    	backURL = (String) request.getAttribute(Constants.BACK_URL);
	    }
	
	    return backURL;
	}
%>