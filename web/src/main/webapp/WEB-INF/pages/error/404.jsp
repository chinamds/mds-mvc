<%@ include file="/common/taglibs.jsp"%>

<head>
    <title><fmt:message key="404.title"/></title>
    <meta name="heading" content="<fmt:message key='404.title'/>"/>
</head>

<p>
    <fmt:message key="404.message">
        <fmt:param><c:url value="${fns:getHomePage(request)}"/></fmt:param>
    </fmt:message>
</p>
<p style="text-align: center">
    <a href="http://www.mmdsplus.com" target="_top"  title="Sunrise Lake QingHai by John Lee, on MDSPlus">
      <img src="http://www.mmdsplus.com/photos/mds/QingHaiLake3.jpg" width="640" height="426" 
      alt="Sunrise Lake QingHai" style="margin: 20px; border: 1px solid black"></a>
</p>