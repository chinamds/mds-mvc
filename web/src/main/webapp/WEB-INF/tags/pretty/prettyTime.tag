<%@ tag import="com.mds.aiotplayer.common.utils.PrettyTimeUtils" %>
<%@ tag pageEncoding="UTF-8"%>
<%@ attribute name="date" type="java.util.Date" required="true" description="time" %>
<%=PrettyTimeUtils.prettyTime(date)%>