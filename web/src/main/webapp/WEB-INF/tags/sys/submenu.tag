<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ tag import="com.mds.aiotplayer.sys.util.UserUtils" %>
<%@ tag import="com.mds.aiotplayer.sys.model.MenuFunction" %>
<%@ tag import="com.mds.aiotplayer.sys.util.MenuComponent" %>
<%@ tag import="com.mds.aiotplayer.core.ResourceId" %>
<%-- <%@ include file="/common/tagcommlibs.jsp"%> --%>
<%-- <%@tag pageEncoding="UTF-8" description="create sub menu" %> --%>
<%-- <%@ attribute name="menus" type="java.util.List" required="true" description="all menu" %> --%>
<%@ attribute name="menu" type="com.mds.aiotplayer.sys.util.MenuComponent" required="true" description="Current menu" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sys" tagdir="/WEB-INF/tags/sys" %>

<c:choose>
    <c:when test="${ menu.menuDepth <= 1 }">
    	<c:if test="${empty menu.image}">
        	<li class="nav-item"><a class="nav-link" href="${menu.url}">${menu.title}</a></li>
        </c:if>
        <c:if test="${not empty menu.image}">
        	<li class="nav-item"><a class="nav-link" href="${menu.url}"><span class="${menu.image}"></span>${menu.title}</a></li>
        </c:if>
    </c:when>
    <c:otherwise>
        <li class="nav-item">
            <a class="nav-link has-arrow" href="#">${menu.title}</a>
            <ul class="mm-collapse" aria-expanded="false">
                <c:forEach items="${menu.components}" var="menu2">
                   	<sys:submenu menu="${menu2}" />
                </c:forEach>
            </ul>
        </li>
    </c:otherwise>
</c:choose>

