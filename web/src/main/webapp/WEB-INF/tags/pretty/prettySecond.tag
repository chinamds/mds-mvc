<%@ tag import="com.mds.aiotplayer.common.utils.PrettyTimeUtils" %>
<%@ tag pageEncoding="UTF-8"%>
<%@ attribute name="seconds" type="java.lang.Integer" required="true" description="second" %>
<%=PrettyTimeUtils.prettySeconds(seconds == null ? 0 : seconds)%>