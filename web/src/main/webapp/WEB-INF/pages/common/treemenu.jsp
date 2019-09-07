<%@ include file="/common/taglibs.jsp"%>

<c:set var="firstMenu" value="true"/>
<div class="sidebar-nav">
<ul id="menu" class="metismenu">
    <c:forEach items="${menus}" var="m">
    	<c:if test="${m.isTop()}">
	        <li class="nav-item ${firstMenu?'mm-active':''}">
	        	<c:if test="${empty m.icon}">
	        		<a class="nav-link has-arrow" href="#" aria-expanded="${firstMenu?'true':'false'}"><fmt:message key="${m.title}"/></a>
		        </c:if>
		        <c:if test="${not empty m.icon}">
		        	<a class="nav-link has-arrow" href="#" aria-expanded="${firstMenu?'true':'false'}"><span class="${m.icon}"></span><fmt:message key="${m.title}"/></a>
		        </c:if>	            
	            <ul class="submenu mm-collapse">
	                <c:forEach items="${menus}" var="c">
	                	<c:if test="${c.parent == m}">
	                        <sys:submenu menu="${c}" menus="${menus}"/>
	                    </c:if>
	                </c:forEach>
	            </ul>
	        </li>
	        <c:if test="${firstMenu}">
	        	<c:set var="firstMenu" value="false"/>
	        </c:if>
	    </c:if>
    </c:forEach>
</ul>
</div>
