<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>

<html style="overflow-x:auto;overflow-y:auto;">
<head>
	<meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no, maximum-scale=1.0, user-scalable=no">
    <link rel="icon" href="<c:url value="/static/images/favicon.ico"/>"/>
    <meta name="_csrf" content="${_csrf.token}"/>
    <!-- default header name is X-CSRF-TOKEN -->
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <title><decorator:title/> | <fmt:message key="webapp.name"/></title>
    <t:assets type="css"/>
    <decorator:head/>
</head>

<body<decorator:getProperty property="body.id" writeEntireProperty="true"/><decorator:getProperty property="body.class" writeEntireProperty="true"/>>
    <c:set var="currentMenu" scope="request"><decorator:getProperty property="meta.menu"/></c:set>

	<c:set var="currentContainer" scope="request"><decorator:getProperty property="meta.container"/></c:set>
    <c:set var="mdsClientId" scope="request"><decorator:getProperty property="meta.mdsClientId"/></c:set>
    <c:set var="mdsShowAlbumMenu" scope="request"><decorator:getProperty property="meta.mdsShowAlbumMenu" default="true"/></c:set>
	<div class="navbar navbar-expand-sm navbar-default navbar-light fixed-top navbar-fixed-top bg-light py-0 ${fns:isSysUserLogin() ? 'd-sm-none':''}" role="navigation">
		<a class="navbar-brand" href="<c:url value='/'/>"><fmt:message key="webapp.name"/></a>
           <button type="button" class="navbar-toggle navbar-toggler" aria-expanded="false" aria-controls="navbar" aria-label="Toggle navigation" data-toggle="collapse" data-target="#navbar">
               <span class="icon-bar navbar-toggler-icon"></span>
           </button>

        <%@ include file="/common/menu.jsp" %>
    </div>    
    
	<c:if test="${empty currentContainer}">
    <div class="container" id="content">
        <%@ include file="/common/messages.jsp" %>
        <div class="row">
            <decorator:body/>
        </div>
    </div>
    </c:if>
    
    <c:if test="${currentContainer eq 'body'}">
         <decorator:body/>
    </c:if>
    
    <c:if test="${currentContainer eq 'galleryView'}">
	    <div id="${mdsClientId}" class="mds_ns">
	          <c:if test="${mdsShowAlbumMenu}">
	    	     <%@ include file="/common/albummenu.jsp" %>
	    	  </c:if>
	    	  <decorator:body/>
	    </div>
    </c:if>
	
	<%-- <c:if test="${fns:isMobileDevice(pageContext.request) and currentContainer ne 'galleryView'}"> --%>
	<c:if test="${currentContainer ne 'galleryView'}">
		<div id="footer" class="container fixed-bottom navbar-fixed-bottom d-sm-none">
	        <span class="col-sm-6 text-left"><fmt:message key="webapp.fullname"/>&nbsp;<fmt:message key="webapp.version"/>
		        <c:if test="${pageContext.request.remoteUser != null}">
		            | <fmt:message key="user.status"/> ${pageContext.request.remoteUser}
	            </c:if> 
	        </span>
	        <span class="col-sm-6 text-right">
	            &copy; <fmt:message key="copyright.year"/> <a href="<fmt:message key="webapp.url"/>"><fmt:message key="webapp.company"/></a> 
	            <c:if test="${not empty fns:getFilingNo()}">
	               <a href="http://beian.miit.gov.cn"  target="_blank">${fns:getFilingNo()}</a>
	            </c:if>
	        </span>
	    </div>
    </c:if>
<t:assets type="js"/>
<script type="text/javascript">
	if (typeof window.Mds === "undefined" || !window.Mds){
		window.Mds = {};
	}
	window.Mds.AppRoot = "${ctx}";
	window.Mds.locale = "${languageTag}";
	if (!(typeof(PNotify) === "undefined") && PNotify){
		//PNotify.defaults.styling = 'bootstrap4'; // Bootstrap version 4
		//PNotify.defaults.icons = 'fontawesome5'; // fontawesome version 5
		PNotify.defaultModules.set(PNotifyBootstrap4, {});
        PNotify.defaultModules.set(PNotifyFontAwesome5, {});
	}
	
	if (window.Mds.isWidthLessThan(991)){
		$('body').css("padding-top", "50px");
	}else{
		<c:if test="${not fns:isSysUserLogin()}">
	       $('body').css("padding-top", "50px");
		</c:if>
/* 		<c:if test="${pageContext.request.remoteUser == null}">
		   $('body').css("padding-top", "50px");
        </c:if> */
	}
		
</script>
<%-- <c:if test="${fns:isMobileDevice(pageContext.request)}">
<script type="text/javascript">
	$('body').css("padding-top", "50px");
</script>
</c:if>  --%>     
<%= (request.getAttribute("scripts") != null) ?  request.getAttribute("scripts") : "" %>
</body>
</html>
