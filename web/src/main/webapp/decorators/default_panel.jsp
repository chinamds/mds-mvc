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
    <title><decorator:title/> | <fmt:message key="webapp.name"/></title>
    <t:assets type="css"/>
    <decorator:head/>
</head>

<body<decorator:getProperty property="body.id" writeEntireProperty="true"/><decorator:getProperty property="body.class" writeEntireProperty="true"/>>
<div class="container">
        <%@ include file="/common/messages.jsp" %>
        <div class="row">
            <decorator:body/>
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
		/* PNotify.prototype.options.styling = 'bootstrap3'; // Bootstrap version 3
		PNotify.prototype.options.icons = 'bootstrap3'; // glyphicons */
		PNotify.defaults.styling = 'bootstrap4'; // Bootstrap version 3
		PNotify.defaults.icons = 'fontawesome5'; // glyphicons
	}
</script>      
<%= (request.getAttribute("scripts") != null) ?  request.getAttribute("scripts") : "" %>
</body>
</html>
