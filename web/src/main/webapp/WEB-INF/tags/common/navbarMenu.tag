<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ tag import="com.mds.aiotplayer.sys.util.UserUtils" %>
<%@ tag import="com.mds.aiotplayer.sys.util.MenuComponent" %>
<%@ tag import="com.mds.aiotplayer.core.ResourceId" %>

<%@ attribute name="menu" type="com.mds.aiotplayer.sys.util.MenuComponent" required="true" description="Current menu" %>
<%@ attribute name="dropdownmenu" type="java.lang.Boolean" required="false" description="dropdown menu" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fns" uri="/WEB-INF/tlds/fns.tld" %>
<%@ taglib prefix="comm" tagdir="/WEB-INF/tags/common" %>

<c:set var="url" value="${fns:unescapeHtml(menu.location)}"/>
<c:if test="${empty menu.location}">
    <c:set var="url" value="javascript:void(0)"/>
</c:if>
<c:if test="${empty dropdownmenu}">
    <c:set var="dropdownmenu" value="false" />
</c:if>

<c:choose>
    <c:when test="${ menu.menuDepth <= 1 }">
        <li class="${dropdownmenu ? '' : 'nav-item'} ${menu.name == currentMenu ? 'active' : ''}">
            <a class="${dropdownmenu ? 'dropdown-item' : 'nav-link'}" href="${url}" title="${menu.title}" <c:if test="${ not empty menu.target}"> target="${menu.target}"</c:if> <c:if test="${ not empty menu.width}"> style="width: ${menu.width}px"</c:if>><c:if test="${ not empty menu.image}"><span class="${menu.image}"></span></c:if>&nbsp;${menu.title}</a>
        </li>
    </c:when>
    <c:otherwise>
        <li class="${dropdownmenu ? '' : 'nav-item'} dropdown${menu.name == currentMenu ? ' active' : ''}">
            <a class="${dropdownmenu ? 'dropdown-item' : 'nav-link'} dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false" href="${url}" title="${menu.title}" <c:if test="${ not empty menu.target}"> target="${menu.target}"</c:if> <c:if test="${ not empty menu.width}"> style="width: ${menu.width}px"</c:if>><c:if test="${ not empty menu.image}"><span class="${menu.image}"></span></c:if>&nbsp;${menu.title}</a>
            <ul class="dropdown-menu">
             <c:forEach items="${menu.components}" var="menu2">
                 <comm:navbarMenu menu="${menu2}" dropdownmenu="true" />
             </c:forEach>
            </ul>
        </li>
    </c:otherwise>
</c:choose>
