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
<c:if test="${not fns:isMobileDevice(pageContext.request)}">	
	<c:set var="group" value="grp_layout" scope="request" />
</c:if>
<c:if test="${fns:isMobileDevice(pageContext.request)}">    
    <c:set var="group" value="grp_fullcalendar" scope="request" />
</c:if>
    <t:assets type="css"/>
</head>

<c:if test="${not fns:isMobileDevice(pageContext.request)}">
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
<script type="text/javascript">
    if (typeof window.Mds === "undefined" || !window.Mds){
        window.Mds = {};
    }
    window.Mds.AppRoot = "${ctx}";
    window.Mds.locale = "${languageTag}";
    if (!(typeof(PNotify) === "undefined") && PNotify){
        PNotify.defaults.styling = 'bootstrap4'; // Bootstrap version 4
        PNotify.defaults.icons = 'fontawesome5'; // fontawesome version 5
    }
    
    if (window.Mds.isWidthLessThan(991)){
        $('body').css("padding-top", "50px");
    }else{
        <c:if test="${not fns:isSysUserLogin()}">
           $('body').css("padding-top", "50px");
        </c:if>
    }        
</script>   
<%= (request.getAttribute("scripts") != null) ?  request.getAttribute("scripts") : "" %>
</body>
</c:if>
<c:if test="${fns:isMobileDevice(pageContext.request)}">
<body class="welcome">
    <c:set var="scripts" scope="request">
	    <%@ include file="/static/scripts/sys/welcome.js"%>
	</c:set>
	
	<div class="navbar navbar-expand-sm navbar-default navbar-light fixed-top navbar-fixed-top bg-light py-0" role="navigation">
        <a class="navbar-brand" href="<c:url value='/'/>"><fmt:message key="webapp.name"/></a>
           <button type="button" class="navbar-toggle navbar-toggler" aria-expanded="false" aria-controls="navbar" aria-label="Toggle navigation" data-toggle="collapse" data-target="#navbar">
               <span class="icon-bar navbar-toggler-icon"></span>
           </button>

        <%@ include file="/common/menu.jsp" %>
    </div>    
    
	<div style="margin-top: 48px;">
	    <div class="navbar navbar-default navbar-light bg-light py-0 mb-3" role="navigation">
	        <div class="py-0 my-0">
	            <a class="btn btn-link btn-view-info" data-toggle="tooltip" data-placement="bottom" title="<fmt:message key="home.welcome.tip"/>">
	                ${pageContext.request.remoteUser}<fmt:message key="menu.welcome"/>
	            </a>
	            <span class="muted">|</span>
	            &nbsp;
	            <span class="muted">
	                <fmt:message key="home.youhave"/>
	                <a class="btn btn-link btn-view-message no-padding" data-toggle="tooltip" data-placement="bottom" title="<fmt:message key="home.viewunreadmessages"/>">
	                    <span class="badge badge-important badge-pill badge-info">${messageUnreadCount}</span>
	                </a>
	                <fmt:message key="home.unreadmessages"/>
	            </span>
	        </div>
	    </div>
	    <fieldset class="mds-fieldset">
	        <legend>
	            <fmt:message key="home.mycalendar"/>
	            (<span class="badge badge-important badge-pill badge-info" data-toggle="tooltip" data-placement="bottom" title="<fmt:message key="home.lastthreedays.reminder"><fmt:param>${calendarCount}</fmt:param></fmt:message>">${calendarCount}</span>)
	            <i class="fa fa-angle-double-down"></i>
	        </legend>
	        <div id='calendar'></div>
	    </fieldset>
	    <br/>
	    <br/>
	    <br/>
	</div>
<t:assets type="js"/>   
<script type="text/javascript">
    if (typeof window.Mds === "undefined" || !window.Mds){
        window.Mds = {};
    }
    window.Mds.AppRoot = "${ctx}";
    window.Mds.locale = "${languageTag}";
    if (!(typeof(PNotify) === "undefined") && PNotify){
        PNotify.defaults.styling = 'bootstrap4'; // Bootstrap version 4
        PNotify.defaults.icons = 'fontawesome5'; // fontawesome version 5
    }
    
    if (window.Mds.isWidthLessThan(991)){
        $('body').css("padding-top", "50px");
    }else{
        <c:if test="${not fns:isSysUserLogin()}">
           $('body').css("padding-top", "50px");
        </c:if>
    }
</script>
<%= (request.getAttribute("scripts") != null) ?  request.getAttribute("scripts") : "" %>
<v:javascript formName="myCalendar" cdata="false" dynamicJavascript="true" staticJavascript="false"/>
<script type="text/javascript" src="<c:url value='/static/scripts/validator.jsp'/>"></script>
</body>
</c:if>
</html>