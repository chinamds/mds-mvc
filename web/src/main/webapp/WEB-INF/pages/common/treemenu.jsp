<%@ include file="/common/taglibs.jsp"%>

<div class="sidebar-nav">
<ul id="treemenu" class="metismenu">
	<c:if test="${userMenuRepository != null}">
	    <c:forEach items="${userMenuRepository.topMenus}" var="m">
	        <li class="nav-item ${m.name == currentMenu ? 'mm-active':''}">
	        	<a class="nav-link has-arrow" href="#" aria-expanded="${m.name == currentMenu ? 'true':'false'}"><c:if test="${not empty m.image}"><span class="${m.image}"></span>&nbsp;</c:if>${m.title}</a>            
	            <ul class="submenu mm-collapse">
	                <c:forEach items="${m.components}" var="c">
                        <sys:submenu menu="${c}" />
	                </c:forEach>
	            </ul>
	        </li>
	    </c:forEach>
	</c:if>
</ul>
</div>
