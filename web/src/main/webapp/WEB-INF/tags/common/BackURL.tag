<%@ tag pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ tag import="com.mds.common.Constants" %>
<%@ tag import="org.apache.commons.lang.StringUtils" %>
<%@ attribute name="hiddenInput" type="java.lang.Boolean" required="false" description="display it or not" %>
<%
    if(!StringUtils.isEmpty(request.getParameter(Constants.IGNORE_BACK_URL))) {
        return;
    }
    String backURL = (String) request.getAttribute(Constants.BACK_URL);
    if(StringUtils.isBlank(backURL)) {
        return;
    }
    if(hiddenInput != null && hiddenInput.equals(Boolean.TRUE)) {
        out.write("<input type=\"hidden\" name=\"" + Constants.BACK_URL + "\" value=\"" + backURL + "\">");
    } else {
        out.write(backURL);
    }
    return;
%>