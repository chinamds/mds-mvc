<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>
<html lang="en">
<head>
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no">
    <link rel="icon" href="<c:url value="/static/images/favicon.ico"/>"/>
	<title><fmt:message key="home.title"/> | <fmt:message key="webapp.name"/></title>
	<meta name="menu" content="Home"/>
	<c:set var="group" value="grp_layout" scope="request" />
    <t:assets type="css"/>
</head>

<body id="home">
	<c:set var="scripts" scope="request">
		<%@ include file="/static/scripts/sys/home.js"%>
	</c:set>
	<ul id="tabs-menu" class="dropdown-menu">
	    <li><a class="dropdown-item close-current" href="#"><fmt:message key="menu.close"/></a></li>
	    <li><a class="dropdown-item close-others" href="#"><fmt:message key="menu.closeothers"/></a></li>
	    <li><a class="dropdown-item close-all" href="#"><fmt:message key="menu.closeall"/></a></li>
	    <li class="dropdown-divider divider"></li>    
	    <li><a class="dropdown-item close-left-all" href="#"><fmt:message key="menu.closeleftall"/></a></li>
	    <li><a class="dropdown-item close-right-all" href="#"><fmt:message key="menu.closerightall"/></a></li>
	</ul>

	<div class="index-panel"> 
	
    	<div class="tabs-bar tabs-fix-top"></div>
	
	   <iframe id="iframe-tabs-0" tabs="true" class="ui-layout-center"
	            frameborder="0" scrolling="auto" src="${ctx}/welcome"></iframe>
	
	   <%@ include file="common/navmenu.jsp" %>
	   <%-- <%@ include file="/common/userinfo.jsp" %> --%>
	   
	    <div class="ui-layout-north home-header">
	        <%@include file="common/header.jsp"%>
	    </div>
	
	
	    <div class="ui-layout-south">
	        <%@include file="common/footer.jsp"%>
	    </div>
	    <div class="ui-layout-west menu">
	        <%@include file="common/treemenu.jsp"%>
	    </div>
	</div>
<t:assets type="js"/>   
<%= (request.getAttribute("scripts") != null) ?  request.getAttribute("scripts") : "" %>
</body>
</html>