<%--
 paging format
   First <<   1   2   3   4   5   6   7   8   9   10  11>  >> Last
   First <<   1   2   3   4   5   6   7   8   9   ... 11  12 >  >> Last
   First <<   1   2  ...  4   5   6   7   8   9   10 ... 12  13 >  >> Last
   First <<   1   2  ...  5   6   7   8   9   10  11  12  13 >  >> Last
   First <<   1   2  ...  5   6   7   8   9   10  11  ... 13  14 >  >> Last
   First <<   1   2  ...  5   6   7   8   9   10  11  ...   21  22 >  >> Last

--%>
<%@tag pageEncoding="UTF-8" description="paging" %>
<%@ include file="/common/tagcommlibs.jsp"%>
<%@ attribute name="page" type="org.springframework.data.domain.Page" required="true" description="paging" %>
<%@ attribute name="pageSize" type="java.lang.Integer" required="false" description="page size" %>
<%@ attribute name="simple" type="java.lang.Boolean" required="false" description="simple style" %>
<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sys" tagdir="/WEB-INF/tags/sys" %> --%>

<c:if test="${empty pageSize}">
    <c:set var="pageSize" value="${page.size}"/>
</c:if>

<c:set var="displaySize" value="2"/>
<c:set var="current" value="${page.number + 1}"/>

<c:set var="begin" value="${current - displaySize}"/>
<c:if test="${begin <= displaySize}">
    <c:set var="begin" value="${1}"/>
</c:if>
<c:set var="end" value="${current + displaySize}"/>
<c:if test="${end > page.totalPages - displaySize}">
    <c:set var="end" value="${page.totalPages - displaySize}"/>
</c:if>
<c:if test="${end < 0 or page.totalPages < displaySize * 4}">
    <c:set var="end" value="${page.totalPages}"/>
</c:if>

<%-- <div class="table-pagination <c:if test='${simple ne false}'> navbar navbar-default</c:if>"> --%>
<nav aria-label="Page navigation" class="table-pagination">
    <ul class="pagination">
        <c:choose>
            <c:when test="${page.first}">
                <li class="disabled"><a title="<fmt:message key="page.first"/>"><fmt:message key="page.first"/></a></li>
                <li class="disabled"><a title="<fmt:message key="page.prev"/>">&lt;&lt;</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="#" onclick="$.table.turnPage('${pageSize}', 1, this);" title="<fmt:message key="page.first"/>"><fmt:message key="page.first"/></a></li>
                <li><a href="#" onclick="$.table.turnPage('${pageSize}', ${current - 1}, this);" title="<fmt:message key="page.prev"/>">&lt;&lt;</a></li>
            </c:otherwise>
        </c:choose>

        <c:forEach begin="1" end="${begin == 1 ? 0 : 2}" var="i">
            <li <c:if test="${current == i}"> class="active"</c:if>>
                <a href="#" onclick="$.table.turnPage('${pageSize}', ${i}, this);" title="<fmt:message key="page.pagenumber"><fmt:param value="${i}"></fmt:param></fmt:message>">${i}</a>
            </li>
        </c:forEach>

        <c:if test="${begin > displaySize + 1}">
            <li><a>...</a></li>
        </c:if>

        <c:forEach begin="${begin}" end="${end}" var="i">
            <li <c:if test="${current == i}"> class="active"</c:if>>
                <a href="#" onclick="$.table.turnPage('${pageSize}', ${i}, this);" title="<fmt:message key="page.pagenumber"><fmt:param value="${i}"></fmt:param></fmt:message>">${i}</a>
            </li>
        </c:forEach>


        <c:if test="${end < page.totalPages - displaySize}">
            <li><a>...</a></li>
        </c:if>

        <c:forEach begin="${end < page.totalPages ? page.totalPages - 1 : page.totalPages + 1}" end="${page.totalPages}" var="i">
            <li <c:if test="${current == i}"> class="active"</c:if>>
                <a href="#" onclick="$.table.turnPage('${pageSize}', ${i}, this);" title="<fmt:message key="page.pagenumber"><fmt:param value="${i}"></fmt:param></fmt:message>">${i}</a>
            </li>
        </c:forEach>

        <c:choose>
            <c:when test="${page.last}">
                <li class="disabled"><a title="<fmt:message key="page.next"/>">&gt;&gt;</a></li>
                <li class="disabled"><a title="<fmt:message key="page.last"/>"><fmt:message key="page.last"/></a></li>
            </c:when>
            <c:otherwise>
                <li><a href="#" onclick="$.table.turnPage('${pageSize}', ${current + 1}, this);" title="<fmt:message key="page.next"/>">&gt;&gt;</a></li>
                <li><a href="#" onclick="$.table.turnPage('${pageSize}', ${page.totalPages}, this);" title="<fmt:message key="page.last"/>"><fmt:message key="page.last"/></a></li>
            </c:otherwise>
        </c:choose>        
    </ul>
    <form class="navbar-form navbar-right hidden-xs hidden-sm">
		<div class="input-group page-input">
		  <span class="input-group-addon"><fmt:message key="page.goto"/></span>
		  <input type="text" class="form-control" value="${current}" onblur="$.table.turnPage('${pageSize}', $(this).val(), this);">
		  <span class="input-group-addon"><fmt:message key="page.page"/></span>
		</div>
        <div class="input-group page-input">
	        <select class="form-control" onchange="$.table.turnPage($(this).val(), ${current}, this);">
	            <option value="10" <c:if test="${pageSize eq 10}">selected="selected" </c:if>>10</option>
	            <option value="20" <c:if test="${pageSize eq 20}">selected="selected" </c:if>>20</option>
	            <option value="30" <c:if test="${pageSize eq 30}">selected="selected" </c:if>>30</option>
	            <option value="50" <c:if test="${pageSize eq 50}">selected="selected" </c:if>>50</option>
	        </select>
	        <span class="input-group-addon"><fmt:message key="page.total"><fmt:param value="${page.totalPages}" /><fmt:param value="${page.totalElements}" /></fmt:message></span >
        </div>
    </form>
 </nav>
<!-- </div> -->
