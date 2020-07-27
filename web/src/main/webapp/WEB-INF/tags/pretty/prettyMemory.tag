<%@ tag import="com.mds.aiotplayer.common.utils.PrettyMemoryUtils" %>
<%@ tag pageEncoding="UTF-8"%>
<%@ attribute name="byteSize" type="java.lang.Long" required="true" description="byte" %>
<%=PrettyMemoryUtils.prettyByteSize(byteSize)%>