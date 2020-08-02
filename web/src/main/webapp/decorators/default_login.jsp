<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no">
    <link rel="icon" href="<c:url value="/static/images/favicon.ico"/>"/>
    <title><decorator:title/> | <fmt:message key="webapp.name"/></title>
    <t:assets type="css"/>
    <decorator:head/>
</head>
<body<decorator:getProperty property="body.id" writeEntireProperty="true"/><decorator:getProperty property="body.class" writeEntireProperty="true"/>>
    <c:set var="currentMenu" scope="request"><decorator:getProperty property="meta.menu"/></c:set>

    <div class="navbar navbar-expand-sm navbar-default navbar-light fixed-top navbar-fixed-top bg-light py-0" role="navigation">
    	<a class="navbar-brand" href="<c:url value='/'/>"><fmt:message key="webapp.name"/></a>
        <button type="button" class="navbar-toggle navbar-toggler" aria-expanded="false" aria-controls="navbar" aria-label="Toggle navigation" data-toggle="collapse" data-target="#navbar">
            <span class="icon-bar navbar-toggler-icon"></span>
        </button>

        <%@ include file="/common/menu.jsp" %>
        <%-- <%@ include file="/common/userinfo.jsp" %> --%>
    </div>
    
    <div class="container" id="content">
        <%@ include file="/common/messages.jsp" %>
        <div class="row">
            <decorator:body/>
        </div>
    </div>

    <div id="footer" class="container fixed-bottom navbar-fixed-bottom">
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
<t:assets type="js"/>
<script type="text/javascript">
	if (typeof window.Mds === "undefined" || !window.Mds){
		window.Mds = {};
	}
	window.Mds.AppRoot = "${ctx}";
	window.Mds.locale = "${languageTag}";
	if (!(typeof(PNotify) === "undefined") && PNotify){
		/* PNotify.prototype.options.styling = 'bootstrap3'; // Bootstrap version 3
		PNotify.prototype.options.icons = 'bootstrap3'; // glyphicons */
		PNotify.defaults.styling = 'bootstrap4'; // Bootstrap version 3
		PNotify.defaults.icons = 'fontawesome5'; // glyphicons
	}
</script>    
<%= (request.getAttribute("scripts") != null) ?  request.getAttribute("scripts") : "" %>
</body>
</html>
