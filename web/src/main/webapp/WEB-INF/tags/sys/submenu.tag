<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ tag import="com.mds.sys.util.UserUtils" %>
<%@ tag import="com.mds.sys.model.MenuFunction" %>
<%@ tag import="com.mds.core.ResourceId" %>
<%-- <%@ include file="/common/tagcommlibs.jsp"%> --%>
<%-- <%@tag pageEncoding="UTF-8" description="create sub menu" %> --%>
<%@ attribute name="menus" type="java.util.List" required="true" description="all menu" %>
<%@ attribute name="menu" type="com.mds.sys.model.MenuFunction" required="true" description="Current menu" %>
<%@ attribute name="hasChildren" type="java.lang.String" required="false" description="has children menu" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="sys" tagdir="/WEB-INF/tags/sys" %>
<%-- <c:set var="hasChildren" value="no"/> --%>
<c:if test="${empty hasChildren}">
	<c:forEach items="${menus}" var="c1">
		<c:if test="${c1.parent == menu}">
		    <c:set var="hasChildren" value="yes"/>
	   </c:if>
	</c:forEach>
</c:if>
<c:choose>
    <c:when test="${ hasChildren != 'yes' }">
    	<c:if test="${empty menu.icon}">
        	<li class="nav-item"><a class="nav-link" href="<%=menuUrl(request, menu)%>"><fmt:message key="${menu.title}"/></a></li>
        </c:if>
        <c:if test="${not empty menu.icon}">
        	<li class="nav-item"><a class="nav-link" href="<%=menuUrl(request, menu)%>"><span class="${menu.icon}"></span><fmt:message key="${menu.title}"/></a></li>
        </c:if>
    </c:when>
    <c:otherwise>
        <li class="nav-item">
            <a class="nav-link has-arrow" href="#"><fmt:message key="${menu.title}"/></a>
            <ul class="mm-collapse" aria-expanded="false">
                <c:forEach items="${menus}" var="menu2">
                	<c:if test="${menu2.parent == menu}">
                    	<sys:submenu menu="${menu2}" menus="${menus}"/>
                    </c:if>
                </c:forEach>
            </ul>
        </li>
    </c:otherwise>
</c:choose>

<%!
	private static String menuUrl(HttpServletRequest request, MenuFunction menu) {
		String url = menu.getHref();
        if(url.startsWith("http")) {
            return url;
        }
        String ctx = request.getContextPath();
        if (menu.getResourceId() != null && menu.getResourceId() != ResourceId.none) {
     	   url = UserUtils.getResourceUrl(menu.getResourceId());
        }

        if(url.startsWith(ctx) || url.startsWith("/" + ctx  )) {
            return url;
        }

        if(!url.startsWith("/")) {
            url = url + "/";
        }
        
        return ctx + url;
    }
%>

